package com.hbwxz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Hello world!
 *
 */
@SpringBootApplication
@EnableDiscoveryClient //开启服务注册发现
@EnableFeignClients //开启feign
public class GatewayApplication
{
    public static void main( String[] args ) {
        SpringApplication.run(GatewayApplication.class,args);
    }
}
