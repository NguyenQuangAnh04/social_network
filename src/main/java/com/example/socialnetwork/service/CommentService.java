package com.example.socialnetwork.service;

import com.example.socialnetwork.dto.CommentDTO;
import com.example.socialnetwork.model.Comment;
import com.example.socialnetwork.model.Post;
import com.example.socialnetwork.model.User;
import com.example.socialnetwork.repository.CommentRepository;
import com.example.socialnetwork.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CommentService implements ICommentService {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CurrentUserService currentUserService;
    @Override
    public Comment createComment(CommentDTO commentDTO) {
        User user =currentUserService.getUserCurrent();
        Post post = postRepository.findById(commentDTO.getPostId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy bài viết"));
        Comment newComment = new Comment();
        newComment.setContent(commentDTO.getContent());
        newComment.setCreatedAt(LocalDateTime.now());
        if(commentDTO.getImageUrl() != null) newComment.setImageUrl(commentDTO.getImageUrl());
        newComment.setPost(post);
        newComment.setUser(user);
        return commentRepository.save(newComment);
    }

    @Override
    @Transactional
    public Comment replyComment(CommentDTO commentDTO) {
        User user = currentUserService.getUserCurrent();
        Comment comment = commentRepository.findById(commentDTO.getId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy comment gốc"));
        Post post = postRepository.findById(commentDTO.getPostId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy bài viết"));
        Comment replyComment = new Comment();
        replyComment.setParent(comment);
        replyComment.setContent(commentDTO.getContent());
        replyComment.setCreatedAt(LocalDateTime.now());
        if(commentDTO.getImageUrl() != null) comment.setImageUrl(commentDTO.getImageUrl());
        replyComment.setUser(user);
        replyComment.setPost(post);
        return commentRepository.save(replyComment);
    }
}
