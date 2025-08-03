package com.example.socialnetwork.dto;

import lombok.Data;

@Data

public class LikeResponseDTO {
    private Long userId;
    private Long commentId;
    private Long totalLike;
}
