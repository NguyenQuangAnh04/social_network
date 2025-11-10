package com.example.socialnetwork.controller;

import com.example.socialnetwork.service.RedisOnlineService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/online")
public class OnlineController {

    private final RedisOnlineService onlineService;

    @GetMapping("/{userId}")
    public boolean isOnline(@PathVariable Long userId) {
        return onlineService.isOnline(userId);
    }
}
