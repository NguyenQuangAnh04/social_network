package com.example.socialnetwork.dto;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class NotificationRequest {
    private Long receiverId;
    private String type; // LIKE, COMMENT, MESSAGE
    private String content;
}
