package com.example.socialnetwork.dto;

import lombok.Data;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

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
    private Long parentComment;
}
