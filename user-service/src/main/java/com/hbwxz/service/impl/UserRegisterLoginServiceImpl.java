package com.hbwxz.service.impl;

import com.alibaba.fastjson.JSON;
import com.hbwxz.dto.reponse.UserRegisterLoginResp;
import com.hbwxz.enums.AuthGrantType;
import com.hbwxz.enums.RegisterType;
import com.hbwxz.pojo.OauthClientDetails;
import com.hbwxz.pojo.OauthUser;
import com.hbwxz.processor.RedisCommonProcessor;
import com.hbwxz.repo.OauthClientDetailsRepository;
import com.hbwxz.repo.OauthUserRepository;
import com.hbwxz.response.CommonResponse;
import com.hbwxz.response.ResponseCode;
import com.hbwxz.response.ResponseUtils;
import com.hbwxz.service.UserRegisterLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author Night
 * @date 2023/7/16 14:11
 */
@Service
public class UserRegisterLoginServiceImpl implements UserRegisterLoginService {

    @Autowired
    private OauthClientDetailsRepository oauthClientDetailsRepository;

    @Autowired
    private OauthUserRepository oauthUserRepository;

    @Autowired
    private RedisCommonProcessor redisCommonProcessor;

    @Autowired
    private RestTemplate innerRestTemplate;

    @Resource(name = "transactionManager")
    private JpaTransactionManager transactionManager;

    /**
     * 用户名密码注册
     * propagation = Propagation.REQUIRED 当前如果存在事物，就加入到当前事物中，如果不存在事物，就创建一个事物
     * 操作了两张表，所以需要在同一个事物中
     * @param user
     * @return
     */
    // @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public CommonResponse namePasswordRegister(OauthUser user) {
        if (oauthUserRepository.findByUserName(user.getUserName()) == null
                && oauthClientDetailsRepository.findByClientId(user.getUserName()) == null) {
            // oauthUser 信息组装
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            // 获取明文密码替换成密文密码
            String password = user.getPassword();
            String encode = encoder.encode(password);
            user.setPassword(encode);

            // OauthClientDetails 信息组装
            OauthClientDetails oauth2Client = OauthClientDetails.builder()
                    .clientId(user.getUserName())
                    .clientSecret(encode)
                    .resourceIds(RegisterType.USER_PASSWORD.name())
                    // 密码模式和刷新token模式
                    .authorizedGrantTypes(AuthGrantType.refresh_token.name().concat(",").concat(AuthGrantType.password.name()))
                    .scope("web") // 作用域
                    .authorities(RegisterType.USER_PASSWORD.name()) // 当前登录的是什么方式
                    .build();
            // start 事物
            Integer uid = this.saveUserAndOauthClient(user, oauth2Client);
            // end 事物

            // 可以自己制定规则，这里使用id+1000000
            // 好处在于尽量减少了表中其他索引的创建。例如将来redis没有数据，可以通过这个值减去1000000，查询数据库，直接查询primary key
            String personId = uid + 1000000 + "";
            redisCommonProcessor.set(personId, user);

            UserRegisterLoginResp registerLoginResp = new UserRegisterLoginResp();
            registerLoginResp.setOauthUser(user);
            registerLoginResp.setClientDetails(generateOauthToken(AuthGrantType.password, user.getUserName(), password, user.getUserName(), password));

            return ResponseUtils.success(registerLoginResp);
        }
        return ResponseUtils.fail(ResponseCode.BAD_REQUEST.getCode(), "用户名已存在", null);
        // 事物结束，进行commit
    }

    /**
     * 方法抽调：保存用户信息和oauth2客户端信息到DB
     * @param user
     * @param oauth2Client
     * @return
     */
    private Integer saveUserAndOauthClient(OauthUser user, OauthClientDetails oauth2Client) {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setTimeout(30); // 超过30秒就回滚
        TransactionStatus status = transactionManager.getTransaction(def);

        try {
            user = this.oauthUserRepository.save(user);
            this.oauthClientDetailsRepository.save(oauth2Client);
            transactionManager.commit(status);
        } catch (Exception e) {
            if (!status.isCompleted()) {
                transactionManager.rollback(status);
            }
            throw new UnsupportedOperationException("保存用户信息和oauth2客户端信息到DB失败");
        }
        return user.getId();
    }


    private OauthClientDetails generateOauthToken(AuthGrantType authGrantType, String username, String password,
                                   String clientId, String clientSecret) {
        // 1. 生成token
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", authGrantType.name());
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        if (authGrantType == AuthGrantType.password) {
            params.add("username", username);
            params.add("password", password);
        }
        HttpEntity<MultiValueMap<String, String>> requestEntity =
                new HttpEntity<>(params, httpHeaders);
        Map map = innerRestTemplate.postForObject("http://oauth2-service/oauth/token", requestEntity, Map.class);
        OauthClientDetails oauthClientDetails = JSON.parseObject(JSON.toJSONString(map), OauthClientDetails.class);
        return oauthClientDetails;
    }
}
