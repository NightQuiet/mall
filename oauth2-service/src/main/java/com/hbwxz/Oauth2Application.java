package com.hbwxz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

/**
 * Hello world!
 *
 */
@SpringBootApplication
@EnableDiscoveryClient //开启服务注册发现
@EnableResourceServer //开启资源服务
public class Oauth2Application {
    public static void main( String[] args ) {
        SpringApplication.run(Oauth2Application.class,args);
    }
}
