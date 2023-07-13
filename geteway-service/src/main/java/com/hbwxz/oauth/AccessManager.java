package com.hbwxz.oauth;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * @author Night
 * @date 2023/7/12 22:35
 */
@Component
public class AccessManager implements ReactiveAuthorizationManager<AuthorizationContext> {
    /**
     * 无需鉴权的url；存放不需要进行token校验的路径（正则表达式）
     */
    private Set<String> permitAll = new ConcurrentSkipListSet<>();

    /**
     * 正则校验器
     */
    public static final AntPathMatcher antPathMatcher = new AntPathMatcher();

    public AccessManager() {
        // 对于我们获取token的请求，无需进行token校验，必须放行
        permitAll.add("/**/oauth/**");
    }

    /**
     * 决定是否放行的最终函数！！！
     * webFlux Mono 和 Flux
     * @param authentication the Authentication to check
     * @param authorizationContext the object to check
     * @return
     */
    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> authentication, AuthorizationContext authorizationContext) {
       //exchange中包含我们的 request信息，能够获取访问路径。只有获取到访问路径后，才能够
       //跟访问路径的url进行判断，是否放行，若放行则放行；若不放行，DB交互，进行真正的校验
       ServerWebExchange exchange = authorizationContext.getExchange();

       return authentication.map(auth -> {
//            return  new AuthorizationDecision(true);
           //进行核心代码的编写
           String requestPath = exchange.getRequest().getURI().getPath();
           //放行的 path
           if(checkPermit(requestPath)) {
               return new AuthorizationDecision(true);
           }

           if(auth instanceof OAuth2Authentication) {
               OAuth2Authentication oAuth2Authentication = (OAuth2Authentication) auth;
               String clientId = oAuth2Authentication.getOAuth2Request().getClientId();
               if(StringUtils.isNotEmpty(clientId)) {
                   return new AuthorizationDecision(true);
               }
           }
           return new AuthorizationDecision(false);
       });

   }

    /**
     * 判断是否为无需鉴权的url
     * @param path
     * @return
     */
    private boolean checkPermit(String path) {
        return permitAll.stream().filter(r -> antPathMatcher.match(r, path)).findFirst().isPresent();
    }
}
