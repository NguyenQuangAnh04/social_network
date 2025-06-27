package com.example.socialnetwork.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final String ONLINE_USER_PREFIX = "online:user";

    public void setUserOnline(Long userId){
        String key = ONLINE_USER_PREFIX + userId;
        stringRedisTemplate.opsForValue().set(key, "true", Duration.ofSeconds(60));
    }
    public void setUserOffline(Long userId){
        String key = ONLINE_USER_PREFIX + userId;
        stringRedisTemplate.delete(key);
    }
    public boolean isUserOnline(Long userId){
        String key = ONLINE_USER_PREFIX + userId;
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(key));
    }
}
