package com.example.socialnetwork.repository;

import com.example.socialnetwork.model.Follow;
import com.example.socialnetwork.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    @Query("SELECT fl FROM Follow fl where fl.follower.id = :followerId AND fl.following.id = :followingId")
    Optional<Follow> findByFollowerIdAndFollowingId(@Param("followerId") Long followerId,
                                                    @Param("followingId") Long followingId);

    void deleteByFollowerAndFollowing(User follower, User following);
    @Query("SELECT fl from Follow fl where fl.follower.id = :followingId")
    List<Follow> findByFollowing(@Param("followingId") Long followingId);
}
