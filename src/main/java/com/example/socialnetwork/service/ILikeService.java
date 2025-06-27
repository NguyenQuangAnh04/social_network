package com.example.socialnetwork.service;

import com.example.socialnetwork.dto.LikeDTO;
import com.example.socialnetwork.model.Like;

public interface ILikeService {
    Like likePost(Long postId);
    void unlikePost(Long postId);
}
