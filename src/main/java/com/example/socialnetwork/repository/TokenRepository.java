package com.example.socialnetwork.repository;

import com.example.socialnetwork.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByRefreshToken(String token);
}
