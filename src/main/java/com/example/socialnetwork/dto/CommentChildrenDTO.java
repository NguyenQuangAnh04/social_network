package com.example.socialnetwork.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class CommentChildrenDTO {
    private Long id;
    private String fullname;
    private String username;
    private LocalDateTime create_at;
    private String content;
    private Long likes_count;
    private Long parent_id;
    private List<CommentChildrenDTO> children;
}
