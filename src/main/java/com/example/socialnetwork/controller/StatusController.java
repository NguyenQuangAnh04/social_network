package com.example.socialnetwork.controller;

import com.example.socialnetwork.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/status")
public class StatusController {
    @Autowired
    private RedisService redisService;

    @PostMapping("/online/{id}")
    public ResponseEntity<?> online(@PathVariable(name = "id") Long id) {
        redisService.setUserOnline(id);
        return ResponseEntity.ok("Set online");
    }

    @PostMapping("/offline/{id}")
    public ResponseEntity<?> offline(@PathVariable(name = "id") Long id) {
        redisService.setUserOffline(id);
        return ResponseEntity.ok("Set offline");
    }

    @GetMapping("/check/{id}")
    public ResponseEntity<?> check(@PathVariable(name = "id") Long id) {
        boolean online = redisService.isUserOnline(id);
        return ResponseEntity.ok(online ? "online" : "offline");
    }

}
