package com.example.socialnetwork.repository;

import com.example.socialnetwork.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByRefreshToken(String token);
    void deleteByRefreshToken(String refreshToken);
    @Query("SELECT CASE " +
            "WHEN COUNT(tk) > 0 THEN TRUE " +
            "ELSE FALSE END FROM Token tk " +
            "WHERE tk.token = :token")
    boolean existByToken(@Param("token") String token);
}
