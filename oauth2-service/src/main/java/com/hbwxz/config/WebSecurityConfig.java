package com.hbwxz.config;

import com.hbwxz.service.UserDetailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author Night
 * @date 2023/7/10 22:58
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailServiceImpl userDetailService;

    /**
     * AuthenticationManager这个东西怎么进行@Autowired注入呢？
     * @return
     * @throws Exception
     */
    @Bean
    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    /**
     * authenticationManager（）方法太拉胯了，直接用了super的authenticationManager，有点low
     * 我们可以控制super.authenticationManager()方法中所使用的authenticationManagerBuilder
     * @param auth
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailService).passwordEncoder(new PasswordEncoder() {
            @Override
            public String encode(CharSequence rawPassword) {
                String encode = new BCryptPasswordEncoder().encode(rawPassword);
                return encode;
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                boolean matches = new BCryptPasswordEncoder().matches(rawPassword, encodedPassword);
                return matches;
            }
        });
    }

    /**
     * 所有的访问都需要oauth2的权限吗？api-doc、swagger-ui、oauth/check_token，过滤点东西
     * @param web
     * @throws Exception
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().mvcMatchers("/oauth/check_token");
    }

    /**
     * 跨域问题 cors、csrf
     * 为什么disable csrf，因为csrf是防止跨站请求伪造，但是我们的oauth2是通过token来进行验证的，所以不需要csrf
     * spring security默认是开启csrf的，所以我们需要禁用掉
     * csrf和restful风格的接口（post）不兼容，所以需要禁用掉
     * csrf默认支持：GET、HEAD、TRACE、OPTIONS，不支持：POST
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests().anyRequest().authenticated()
                .and().httpBasic()
                .and().cors()
                .and().csrf().disable();
    }
}
