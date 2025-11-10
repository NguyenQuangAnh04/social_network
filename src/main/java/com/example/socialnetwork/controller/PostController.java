package com.example.socialnetwork.controller;

import com.example.socialnetwork.dto.PostDTO;
import com.example.socialnetwork.dto.UserProfileResponse;
import com.example.socialnetwork.model.Post;
import com.example.socialnetwork.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/post")
@Slf4j
public class PostController {
    @Autowired
    private PostService postService;

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Post> createPost(@RequestPart("postDTO") PostDTO postDTO,
                                           @RequestPart(value = "file", required = false) MultipartFile file) throws IOException {
        return ResponseEntity.ok(postService.createPost(postDTO, file));
    }

    @GetMapping()
    public ResponseEntity<List<PostDTO>> findAll() {
        return ResponseEntity.ok(postService.findAll());
    }

    @GetMapping("/user")
    public ResponseEntity<UserProfileResponse> getAllPostByUser() {
        return ResponseEntity.ok(postService.getAllPostByUser());
    }

    @GetMapping("/user/search")
    public ResponseEntity<UserProfileResponse> findAllPostByUser(@RequestParam(name = "name") String name) {
        return ResponseEntity.ok(postService.findAllPostByUser(name));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deletePostById(@PathVariable(name = "id") Long id) {
        postService.deletePostById(id);
        log.info("Xóa bài viết {} thành công", id);
        return ResponseEntity.ok("Xóa bài viết thành công");
    }

    @PostMapping("/edit")
    public ResponseEntity<PostDTO> edit(@RequestBody PostDTO postDTO) {
        return ResponseEntity.ok(postService.editPost(postDTO));
    }
}
