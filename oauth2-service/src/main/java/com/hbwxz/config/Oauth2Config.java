package com.hbwxz.config;

import com.hbwxz.service.UserDetailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

import javax.sql.DataSource;

/**
 * EnableAuthorizationServer 开启授权服务
 * @author Night
 * @date 2023/7/10 22:17
 */
@Configuration
@EnableAuthorizationServer
public class Oauth2Config extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private UserDetailServiceImpl userDetailService;

    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * 配置token存储到数据库
     * @return TokenStore
     */
    @Bean
    public TokenStore tokenStore(){
        return new JdbcTokenStore(dataSource);
    }

    /**
     * token 是不是得有过期时间，如果有过期时间，那么过期时间是多少？
     * oauth2的默认过期时间是12小时，如果想要自定义过期时间，那么就需要配置tokenServices
     * 然后就需要用到defaultTokenServices，并进行配置
     * @return DefaultTokenServices
     */
    @Bean
    @Primary
    public DefaultTokenServices tokenServices(){
        DefaultTokenServices services = new DefaultTokenServices();
        // 30天过期
        services.setAccessTokenValiditySeconds(30*24*3600);
        // 配置token存储到数据库
        services.setTokenStore(tokenStore());
        return services;
    }

    /**
     * 配置客户端信息从数据库中读取
     * 关心一下我们的client details中的内容：client_id、client_secret，从哪里来？
     * 带大家进行实际的postman的调用，并且为大家演示四种token获取方式，到时候就知道
     * username+password和client_id+client_secret的区别和联系了
     * @return ClientDetailsService
     */
    @Bean
    public ClientDetailsService clientDetails(){
        return new JdbcClientDetailsService(dataSource);
    }

    /**
     * 添加自定义的安全的配置，可以选择不添加
     * 往往我们会将这个配置用于：放开一些接口的查询权限，比如说check_token接口
     * allowFormAuthenticationForClients 允许表单认证
     * checkTokenAccess 允许check_token访问
     * @param security
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.allowFormAuthenticationForClients()
                .checkTokenAccess("permitAll()");
    }

    /**
     *
     * @param clients
     * @throws Exception
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.withClientDetails(clientDetails());
    }

    /**
     * 配置oauth2的主配置信息（终端）
     * @param endpoints
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.tokenServices(tokenServices())
                .userDetailsService(userDetailService)
                // .tokenStore(tokenStore())
                .authenticationManager(this.authenticationManager);
    }

    /**
     * 配置密码加密方式
     * @return PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
        // 如果企业要求自己的加密算法，可以通过这种形式进行encode以及校验是否相符
        // return new PasswordEncoder() {
        //     @Override
        //     public String encode(CharSequence rawPassword) {
        //         return null;
        //     }
        //
        //     @Override
        //     public boolean matches(CharSequence rawPassword, String encodedPassword) {
        //         return false;
        //     }
        // }
    }


}
