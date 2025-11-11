package com.example.socialnetwork.service;

import com.example.socialnetwork.dto.FriendDTO;

import java.util.List;

public interface IFollowService {
    boolean followUser( Long followingId);
    boolean unfollowUser( Long followingId);
    List<FriendDTO> findAll();
    List<FriendDTO> findAllFriendByUser(Long userId);

}
