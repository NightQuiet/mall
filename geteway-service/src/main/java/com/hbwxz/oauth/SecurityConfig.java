// package com.hbwxz.oauth;
//
//
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.http.HttpMethod;
// import org.springframework.security.authentication.ReactiveAuthenticationManager;
// import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
// import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
// import org.springframework.security.config.web.server.ServerHttpSecurity;
// import org.springframework.security.oauth2.server.resource.web.server.ServerBearerTokenAuthenticationConverter;
// import org.springframework.security.web.server.SecurityWebFilterChain;
// import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
//
// import javax.sql.DataSource;
//
// /**
//  * EnableWebFluxSecurity 开启webflux安全性
//  * @author Night
//  * @date 2023/7/13 8:56
//  */
// @Configuration
// @EnableWebFluxSecurity
// public class SecurityConfig {
//
//     @Autowired
//     private DataSource dataSource;
//
//     @Autowired
//     private AccessManager accessManager;
//     @Bean
//     SecurityWebFilterChain webFluxSecurityFilterChain(ServerHttpSecurity serverHttpSecurity) {
//         // 我们的 AuthenticationWebFilter 过滤器 有一个必要的组件，就是我们的 ReactiveAuthenticationManager 权限管理器；
//         // 权限管理器需要底层数据层的支持，需要DB连接资源类dataSource
//         ReactiveAuthenticationManager reactiveAuthenticationManager
//                = new ReactiveJdbcAuthenticationManager(dataSource);
//
//         AuthenticationWebFilter authenticationWebFilter = new AuthenticationWebFilter(reactiveAuthenticationManager);
//         authenticationWebFilter.setServerAuthenticationConverter(new ServerBearerTokenAuthenticationConverter());
//
//         // TODO 我们需要进行DB交互类的创建，然后再将我们的DataSource搞到当前代码中
//         serverHttpSecurity.httpBasic().disable()
//                 .csrf().disable()
//                 .authorizeExchange()
//                 .pathMatchers(HttpMethod.OPTIONS).permitAll()
//                 .anyExchange().access(accessManager)
//                 .and().addFilterAt(authenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION);
//
//
//         return serverHttpSecurity.build();
//     }
// }
