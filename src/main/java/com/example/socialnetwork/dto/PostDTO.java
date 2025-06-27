package com.example.socialnetwork.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostDTO {
    private String content;
    private Long id;
    private String image_url;
    private Long userId;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
    private String fullName;
    private Long friendId ;
    private List<CommentDTO> commentDTOList;
    private String timeAgo;
    private List<LikeDTO> likeDTOList;
    private Long totalLike;
}
