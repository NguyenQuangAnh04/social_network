package com.example.socialnetwork.service;

import com.example.socialnetwork.dto.PostDTO;
import com.example.socialnetwork.model.Post;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface IPostService {
    Post createPost(PostDTO postDTO) ;

    List<PostDTO> findAll();

    List<PostDTO> getAllPostByUser();

    List<PostDTO> findAllPostByUser(Long userId);

    void deletePostById(Long postId);
}
