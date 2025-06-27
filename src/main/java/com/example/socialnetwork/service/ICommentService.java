package com.example.socialnetwork.service;

import com.example.socialnetwork.dto.CommentDTO;
import com.example.socialnetwork.model.Comment;

public interface ICommentService {
    Comment createComment(CommentDTO commentDTO);

    Comment replyComment(CommentDTO commentDTO);
}
