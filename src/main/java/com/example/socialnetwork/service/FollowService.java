package com.example.socialnetwork.service;

import com.example.socialnetwork.model.Follow;
import com.example.socialnetwork.model.User;
import com.example.socialnetwork.repository.FollowRepository;
import com.example.socialnetwork.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class FollowService implements IFollowService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FollowRepository followRepository;
    @Autowired
    private CurrentUserService currentUserService;

    @Override
    public boolean followUser(Long followingId) {
        Optional<User> user = userRepository.findById(followingId);
        if (user.isEmpty()) throw new EntityNotFoundException("Not found userId");
        Optional<Follow> checkFollowBetween2User = followRepository.findByFollowerIdAndFollowingId(currentUserService.getUserCurrent().getId(), user.get().getId());
        if (checkFollowBetween2User.isEmpty()) {
            Follow follow = new Follow();
            follow.setFollowing(user.get());
            follow.setFollower(currentUserService.getUserCurrent());
            follow.setCreatedAt(LocalDateTime.now());
            followRepository.save(follow);
            return true;
        } else {
            return false;
        }
    }

    @Override
    @Transactional
    public boolean unfollowUser(Long followingId) {
        Optional<User> user = userRepository.findById(followingId);
        if (user.isEmpty()) throw new EntityNotFoundException("Not found userId");
        Optional<Follow> checkFollowBetween2User = followRepository.findByFollowerIdAndFollowingId(currentUserService.getUserCurrent().getId(), user.get().getId());
        if (checkFollowBetween2User.isEmpty()) {
            return false;
        } else {
            followRepository.deleteByFollowerAndFollowing(currentUserService.getUserCurrent(), user.get());
            return true;
        }
    }
}
