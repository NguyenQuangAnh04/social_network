package com.example.socialnetwork.dto;

import lombok.Data;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CommentDTO {
    private Long id;
    private String imageUrl;
    private String content;
    private String timeAgo;
    private LocalDateTime updated_at;
    private Long userId;
    private String fullName;
    private Long postId;
    private String avatar;
    private List<Long> users;
    private Long parentComment;
    private Long totalComment;
    private List<CommentDTO> children;
    private Long totalLike;
}
