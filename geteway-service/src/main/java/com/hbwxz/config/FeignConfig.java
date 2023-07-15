package com.hbwxz.config;

import feign.codec.Decoder;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.ArrayList;
import java.util.List;

/**
 *  HttpMessageConverters 这个是我们http请求的msg转化器，既然2.4说明了，openfeign和gateway的调用机制有冲突，那么我们在2.4解决了这个
 *  同步异步问题，但是没结果msg转化问题，所以还需要继续解决msg convert。
 *  我们需要在这里边完成message的converter
 * @author Night
 * @date 2023/7/14 8:43
 */
@Configuration
public class FeignConfig {

    @Bean
    public Decoder feignDecoder() {
        // 我们需要在这里边完成message的converter
        ObjectFactory<HttpMessageConverters> objectFactory = new ObjectFactory() {
            @Override
            public HttpMessageConverters getObject() throws BeansException {
                MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter
                        = new MappingJackson2HttpMessageConverter();
                List<MediaType> mediaTypes = new ArrayList<>();
                mediaTypes.add(MediaType.valueOf(MediaType.TEXT_HTML_VALUE + ";charset=UTF-8"));
                mappingJackson2HttpMessageConverter.setSupportedMediaTypes(mediaTypes);
                final HttpMessageConverters httpMessageConverters
                        = new HttpMessageConverters(mappingJackson2HttpMessageConverter);
                return httpMessageConverters;
            }
        };

        return new ResponseEntityDecoder(new SpringDecoder(objectFactory));
    }
}
