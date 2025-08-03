package com.example.socialnetwork.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserProfileResponse {
    private UserDTO user;
    private List<PostDTO> posts;

}
