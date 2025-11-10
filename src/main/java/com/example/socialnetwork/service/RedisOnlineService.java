package com.example.socialnetwork.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisOnlineService {
    private final StringRedisTemplate template;

    private static final Duration TTL = Duration.ofSeconds(60);

    public void setUserOnline(Long userId){
        template.opsForValue().set("online:user:" + userId, "online", TTL);
    }

    public void refreshUserOnline(Long userId){
        template.expire("online:user:" + userId, TTL);
    }

    public void setUserOffline(Long userId){
        template.delete("online:user:" + userId);
    }


    public boolean isOnline(Long userId){
        return Boolean.TRUE.equals(template.hasKey("online:user:" + userId));
    }

}
