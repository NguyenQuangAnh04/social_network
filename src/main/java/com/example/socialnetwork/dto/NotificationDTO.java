package com.example.socialnetwork.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationDTO {
    private Long id;
    private Long senderId;
    private String fullName;
    private Long receiverId;
    private String type; // LIKE, COMMENT, MESSAGE
    private String content;
    private String avatar;
    private String createdAt;
    private boolean isRead;
    private Long postId;
    private Long commentId;
}
