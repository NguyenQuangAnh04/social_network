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
public class MessageDTO {
    private Long id;
    private String content;
    private String fullName;
    private String avatar;
    private String room;
    private String timeStamp;
    private Long senderId;
    private Long receiverId;
    private int countMessageNotRead;
    private boolean parent;
}
