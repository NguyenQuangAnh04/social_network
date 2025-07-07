package com.example.socialnetwork.repository;

import com.example.socialnetwork.enums.FriendshipStatus;
import com.example.socialnetwork.model.Friendship;
import com.example.socialnetwork.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FriendShipRepository extends JpaRepository<Friendship, Long> {
//    List<Friendship> findByUser(User user);
    @Query("SELECT f FROM Friendship f " +
            "WHERE f.status = 'ACCEPTED' AND " +
            "((f.sender.fullName = :myName AND f.receiver.fullName = :otherName)) OR " +
            "((f.receiver.fullName = :myName AND f.sender.fullName = :otherName))")
Optional<Friendship> findAcceptedFriendshipBetween(
        @Param("myName") String myName,
        @Param("otherName") String otherName);
    @Query("SELECT f FROM Friendship f WHERE " +
            "(f.sender.id = :currentUser AND f.receiver.id = :userId) OR " +
            "(f.receiver.id = :currentUser AND f.sender.id = :userId)")
    Friendship findByUsers(@Param("currentUser") Long currentUser, @Param("userId") Long userId);




}

