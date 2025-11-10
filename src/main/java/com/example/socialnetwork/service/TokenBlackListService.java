package com.example.socialnetwork.service;

import com.example.socialnetwork.component.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TokenBlackListService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final String KEY_PREFIX = "blacklist:";
    private final JwtUtils jwtUtils;

    public void addTokenToBlackList(String token) {
        long remainingMills = jwtUtils.getRemainingTime(token);
        redisTemplate.opsForValue().set(KEY_PREFIX, true, remainingMills, TimeUnit.MINUTES);
    }

    public boolean isTokenBlackListed(String token){
       Boolean exist = redisTemplate.hasKey(KEY_PREFIX + token);
       return Boolean.TRUE.equals(exist);
    }
}
