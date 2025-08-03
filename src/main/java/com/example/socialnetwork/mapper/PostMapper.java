package com.example.socialnetwork.mapper;

import com.example.socialnetwork.dto.PostDTO;
import com.example.socialnetwork.model.Post;
import com.example.socialnetwork.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class PostMapper {


    public Post toEntity(PostDTO dto, User author) {
        Post post = new Post();
        post.setContent(dto.getContent());
        post.setAuthor(author);
        post.setCreatedAt(LocalDateTime.now());
        post.setImageUrl(dto.getImage_url());
        return post;
    }

    public PostDTO toDTO(Post post) {
        PostDTO dto = new PostDTO();
        dto.setId(post.getId());
        dto.setContent(post.getContent());
        dto.setUserId(post.getAuthor().getId());
        dto.setFullName(post.getAuthor().getFullName());
        dto.setTimeAgo(formatTimeAgo(post.getCreatedAt()));
        dto.setImage_url(post.getImageUrl());
        return dto;
    }


    private String formatTimeAgo(LocalDateTime createdAt) {
        Duration duration = Duration.between(createdAt, LocalDateTime.now());
        long seconds = duration.getSeconds();
        if (seconds < 60) return "Vừa xong";
        if (seconds < 3600) return seconds / 60 + " phút trước";
        if (seconds < 86400) return seconds / 3600 + " giờ trước";
        if (seconds < 2592000) return seconds / 86400 + " ngày trước";
        if (seconds < 31104000) return seconds / 2592000 + " tháng trước";
        return seconds / 31104000 + " năm trước";
    }
}
