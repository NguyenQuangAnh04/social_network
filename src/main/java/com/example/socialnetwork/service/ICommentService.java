package com.example.socialnetwork.service;

import com.example.socialnetwork.dto.CommentDTO;
import com.example.socialnetwork.model.Comment;

import java.util.List;

public interface ICommentService {
    Comment createComment(CommentDTO commentDTO);
    List<CommentDTO> showRepLyCommentParent(Long parentId, Long postId);
    Comment replyComment(CommentDTO commentDTO);
    List<CommentDTO> findAllByPost(Long id);
}
