package com.hbwxz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Hello world!
 *
 */
@SpringBootApplication
@EnableDiscoveryClient //开启服务注册发现
public class GatewayApplication
{
    public static void main( String[] args ) {
        SpringApplication.run(GatewayApplication.class,args);
    }
}
