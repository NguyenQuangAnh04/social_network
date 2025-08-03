package com.example.socialnetwork.controller;

import com.example.socialnetwork.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/upload")
public class UploadController {
    @Autowired
    private CloudinaryService cloudinaryService;

    @PostMapping("/post/{postId}")
    public ResponseEntity<String> postImage(@RequestParam("file") MultipartFile file, @PathVariable Long postId) throws IOException {
        return ResponseEntity.ok(cloudinaryService.uploadFilePost(file, postId));
    }

//    @PostMapping("/comment/{postId}")
//    public ResponseEntity<String> commentImage(@RequestParam("file")MultipartFile file, Long postId) throws IOException {
//        return ResponseEntity.ok(cloudinaryService.uploadFileComment(file, postId));
//    }
}
