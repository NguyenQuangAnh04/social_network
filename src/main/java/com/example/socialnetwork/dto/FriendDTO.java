package com.example.socialnetwork.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class FriendDTO {
    private Long id;
    private String fullName;
    private String username;
    private String imageUrl;
}
