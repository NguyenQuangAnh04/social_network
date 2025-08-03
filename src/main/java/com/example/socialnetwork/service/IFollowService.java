package com.example.socialnetwork.service;

public interface IFollowService {
    boolean followUser( Long followingId);
    boolean unfollowUser( Long followingId);

}
