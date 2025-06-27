package com.example.socialnetwork.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageDTO {
    private Long id;
    private String content;
    private String fullName;
    private Long userId1;
    private Long userId2;
    private String timeStamp;
}
