package com.example.socialnetwork.repository;

import com.example.socialnetwork.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    @Query("SELECT t from Token  t where t.refreshToken = :token")
    Optional<Token> findByRefreshToken(@Param("token") String token);
    void deleteByRefreshToken(String refreshToken);
    @Query("SELECT t FROM Token t WHERE t.user.id = :userId and t.refreshTokenExpiresAt > current_timestamp ")
    Token findRefreshTokensByUserId(@Param("userId") Long userId);

}
