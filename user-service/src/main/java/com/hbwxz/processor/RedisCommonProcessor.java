package com.hbwxz.processor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author Night
 * @date 2023/7/15 23:03
 */
@Component
public class RedisCommonProcessor {

    @Autowired
    private RedisTemplate redisTemplate;

    public Object get(String key) {
        if (key == null) {
            throw new UnsupportedOperationException("key should not be null!");
        }
        return redisTemplate.opsForValue().get(key);
    }

    public void set(String key, Object value) {
        if (key == null) {
            throw new UnsupportedOperationException("key should not be null!");
        }
        redisTemplate.opsForValue().set(key, value);
    }

    public void set(String key, Object value, long timeSeconds) {
        if (key == null) {
            throw new UnsupportedOperationException("key should not be null!");
        }
        if (timeSeconds <= 0) {
            set(key, value);
        }
        redisTemplate.opsForValue().set(key, value, timeSeconds, TimeUnit.SECONDS);
    }

    public void remove(String key) {
        if (key == null) {
            throw new UnsupportedOperationException("key should not be null!");
        }
        redisTemplate.delete(key);
    }
}
