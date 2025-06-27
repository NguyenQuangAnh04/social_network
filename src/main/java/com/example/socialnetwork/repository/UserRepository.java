package com.example.socialnetwork.repository;

import com.example.socialnetwork.model.Message;
import com.example.socialnetwork.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    List<User> findByFullName( String fullName);
    @Query("SELECT u from User u where u.fullName LIKE %:fullName%")
    List<User> findByName(@Param("fullName") String fullName);
}
