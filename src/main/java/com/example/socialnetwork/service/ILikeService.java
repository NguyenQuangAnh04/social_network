package com.example.socialnetwork.service;

import com.example.socialnetwork.dto.LikeDTO;
import com.example.socialnetwork.dto.LikeResponseDTO;
import com.example.socialnetwork.model.Like;

import java.util.List;

public interface ILikeService {
    Like likePost(Long postId);
    void unlikePost(Long postId);

    Like likeComment(Long commentId);

    List<Long> findHasLikedByUser();
}
