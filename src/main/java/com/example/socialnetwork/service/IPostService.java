package com.example.socialnetwork.service;

import com.example.socialnetwork.dto.PostDTO;
import com.example.socialnetwork.dto.UserProfileResponse;
import com.example.socialnetwork.model.Post;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface IPostService {
    Post createPost(PostDTO postDTO, MultipartFile file) throws IOException ;

    List<PostDTO> findAll();

    UserProfileResponse getAllPostByUser();

    UserProfileResponse findAllPostByUser(String username);
    PostDTO editPost(PostDTO postDTO);
    void deletePostById(Long postId);

    PostDTO findPostById(Long postId);
}
