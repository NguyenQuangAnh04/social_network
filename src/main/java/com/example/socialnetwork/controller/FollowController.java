package com.example.socialnetwork.controller;

import com.example.socialnetwork.dto.FriendDTO;
import com.example.socialnetwork.service.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/follow")
@RestController
public class FollowController {
    @Autowired
    private FollowService followService;

    @PostMapping("/following/{userId}")
    public ResponseEntity<Boolean> follow(@PathVariable(name = "userId") Long userId){
        return ResponseEntity.ok(followService.followUser(userId));
    }

    @DeleteMapping("/unfollow/{userId}")
    public ResponseEntity<Boolean> unFollow(@PathVariable(name = "userId") Long userId){
        return ResponseEntity.ok(followService.unfollowUser(userId));
    }

    @GetMapping("/friend/{userId}")
    public ResponseEntity<List<FriendDTO>> getAllFriend(@PathVariable Long userId){
        return ResponseEntity.ok(followService.findAllFriendByUser(userId));
    }
}
