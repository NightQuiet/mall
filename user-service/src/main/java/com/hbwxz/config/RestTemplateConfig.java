package com.hbwxz.config;

import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;

/**
 * restTemplate底层使用的jdk的url connection，没有什么过期时间的设置，对于一些复杂场景
 * 不太适用，所以我们打算替换掉底层 的url connection，转而使用 apache的 httpclient
 * 并且在 http client中，配置我们的 链接超时，线程池大小，keep-live配置，定时清理空闲线程的任务等等。
 * 整个resttempalte的配置是可控的且多元化，而且比较稳定
 */
@Configuration
public class RestTemplateConfig {

    //你通过 autowired 注入进来就算是替换了？ 玩儿呢
    @Autowired
    private CloseableHttpClient httpClient;

    /**
     * 通过httpclient来创建一个httpRequestFactory，这个factory是用来创建restTemplate的
     * httpClient不能直接注入到restTemplate中，所以我们需要通过httpRequestFactory来创建
     * @return
     */
    @Bean
    public HttpComponentsClientHttpRequestFactory httpRequestFactory(){
        HttpComponentsClientHttpRequestFactory httpRequestFactory
                = new HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setHttpClient(httpClient);
        return httpRequestFactory;
    }

    /**
     * httpClient不能直接注入到restTemplate中，所以我们需要通过httpRequestFactory来创建
     * 并且在restTemplate中设置我们的字符集为utf-8
     * 必须要加@LoadBalanced注解，否则无法使用ribbon的负载均衡也就找不到user-service服务
     * @return
     */
    @Bean
    @LoadBalanced
    public RestTemplate getRestTemplate(){
        RestTemplate restTemplate = new RestTemplate(httpRequestFactory());
        // 入参的时候我们一般接收utf-8形式的消息
        List<HttpMessageConverter<?>> messageConverters = restTemplate.getMessageConverters();
        Iterator<HttpMessageConverter<?>> iterator = messageConverters.iterator();
        while(iterator.hasNext()){
            HttpMessageConverter<?> converter = iterator.next();
            if(converter instanceof StringHttpMessageConverter) {
                ((StringHttpMessageConverter)converter).setDefaultCharset(Charset.forName("UTF-8"));
            }
        }
        return restTemplate;
    }
}
