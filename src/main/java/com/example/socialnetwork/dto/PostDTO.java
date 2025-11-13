package com.example.socialnetwork.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostDTO {
    private String content;
    private Long id;
    private String image_url;
    private Long userId;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
    private String avatar;
    private String fullName;
    private String username;
//    private List<CommentDTO> commentDTOList;
    private String timeAgo;
    private List<LikeDTO> likeDTOList;
    private Long[] savedByUser;
    private Long totalLike;
    private Long totalComment;
}
