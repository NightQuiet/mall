package com.hbwxz.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * @description: Oauth2ServiceClient
 * @author Night
 * @date 2023/7/13 23:01
 */
@FeignClient("oauth2-service")
public interface Oauth2ServiceClient {

    /**
     * 校验token
     * @param token
     * @return
     */
    @RequestMapping("/oauth/check_token")
    Map<String,Object> checkToken(@RequestParam("token") String token);
}
