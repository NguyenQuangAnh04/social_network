package com.example.socialnetwork.controller;

import com.example.socialnetwork.dto.LikeDTO;
import com.example.socialnetwork.model.Like;
import com.example.socialnetwork.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/like")
public class LikeController {
    @Autowired
    private LikeService likeService;

    @PostMapping("/post/{postId}")
    public ResponseEntity<Like> likePost(@PathVariable(name = "postId") Long postId){
        return ResponseEntity.ok(likeService.likePost(postId));
    }

    @DeleteMapping("/unlike/{postId}")
    public ResponseEntity<Void> unlikePost(@PathVariable(name = "postId") Long postId){
        likeService.unlikePost(postId);
        return ResponseEntity.noContent().build();
    }
}
