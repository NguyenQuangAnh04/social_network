package com.example.socialnetwork.component;

import com.example.socialnetwork.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtils {
    @Value("${app.jwtSecret}")
    private String secret;
    @Value("${app.jwtExpirationInMs}")
    private Long expiration;

    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .setId(String.valueOf(user.getId()))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(singingKey(secret), SignatureAlgorithm.HS256)
                .compact();
    }

    public Key singingKey(String secret) {
        byte[] bytes = secret.getBytes();
        return Keys.hmacShaKeyFor(bytes);
    }

    public Date isExpiration(String token) {
        return extractToken(token, Claims::getExpiration);
    }

    public String extractUserName(String token) {
        return extractToken(token, Claims::getSubject);
    }

    public boolean validate(String token, UserDetails userDetails) {
        String username = userDetails.getUsername();
        return (username.equals(extractUserName(token)) && !isExpiration(token).before(new Date()));
    }

    public Long extractUserId(String token){
        String id =  extractToken(token, Claims::getId);
        return Long.valueOf(id);
    }

    public Claims extractAllToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(singingKey(secret))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    public long getRemainingTime(String token){
        return isExpiration(token).getTime() - System.currentTimeMillis();
    }
    public Boolean validateToken(String token){
        try{
            extractAllToken(token);
            return !isExpiration(token).before(new Date());
        }catch (Exception e) {
            return false;
        }
    }
    private <T> T extractToken(String token, Function<Claims, T> claimsFunction) {
        final Claims claims = extractAllToken(token);
        return claimsFunction.apply(claims);
    }
}
