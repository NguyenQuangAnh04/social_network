package com.example.socialnetwork.dto;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

@Data
public class LikeDTO {
    private Long id;
    private Long postId;
    private Long userId;
    private String fullName;
    private String imageUrl;
}
