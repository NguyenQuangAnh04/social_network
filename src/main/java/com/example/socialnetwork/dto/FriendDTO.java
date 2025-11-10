package com.example.socialnetwork.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FriendDTO {
    private Long id;
    private String fullName;
    private String username;
}
