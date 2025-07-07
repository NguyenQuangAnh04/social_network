package com.example.socialnetwork.controller;

import com.example.socialnetwork.component.JwtUtils;
import com.example.socialnetwork.dto.UserDTO;
import com.example.socialnetwork.model.Token;
import com.example.socialnetwork.model.User;
import com.example.socialnetwork.repository.TokenRepository;
import com.example.socialnetwork.repository.UserRepository;
import com.example.socialnetwork.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private JwtUtils jwtUtils;
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody() UserDTO userDTO) {
        Optional<User> user = userRepository.findByUsername(userDTO.getUsername());
        if (user.isEmpty()) throw new EntityNotFoundException("Kiểm tra lại tài khoản hoặc mật khẩu");
        String access_token = userService.login(userDTO.getUsername(), userDTO.getPassword());
        String refreshToken = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();
        Token token = Token.builder()
                .token(access_token)
                .refreshToken(refreshToken)
                .user(user.get())
                .tokenCreatedAt(now)
                .refreshTokenCreatedAt(now)
                .refreshTokenIssuedAt(now)
                .lastUsedAt(now)
                .refreshTokenExpiresAt(now.plus(30, ChronoUnit.DAYS))
                .build();
        tokenRepository.save(token);
        Map<String, String> response = new HashMap<>();
        response.put("access_token", access_token);
        response.put("refresh_token", refreshToken);
        response.put("expires_in", "30 ngày");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<Map<String, String>> refreshToken(@RequestBody() Map<String, String> request) {
        try {
            String refreshToken = request.get("refresh_token");
            if(refreshToken == null  || refreshToken.trim().isEmpty()){
                throw new IllegalArgumentException("Refresh token không được bỏ trống");
            }
            Optional<Token> tokenOptional = tokenRepository.findByRefreshToken(refreshToken);
            if (tokenOptional.isEmpty()){
                throw new EntityNotFoundException("Refresh token không hợp lệ");
            }
            Token token = tokenOptional.get();
            LocalDateTime now = LocalDateTime.now();
            if(token.getRefreshTokenExpiresAt().isAfter(now)){
                String newAccessToken = jwtUtils.generateToken(token.getUser());
                token.setToken(newAccessToken);
                token.setTokenCreatedAt(now);
                token.setLastUsedAt(now);
                tokenRepository.save(token);
                Map<String, String> response = new HashMap<>();
                response.put("access_token", newAccessToken);
                response.put("message", "Access token đã được làm mới");
                return ResponseEntity.ok(response);
            }
            LocalDateTime thirtyDaysAgo = now.minus(30, ChronoUnit.DAYS);
            log.info(thirtyDaysAgo.toString());
            if(token.getLastUsedAt().isBefore(thirtyDaysAgo)){
                tokenRepository.delete(token);
                throw new RuntimeException("Tài khoản không họạt động quá 30 ngày. Vui lòng đăng nhập lại");
            }
            String newAccessToken = jwtUtils.generateToken(token.getUser());
            token.setToken(newAccessToken);
            token.setTokenCreatedAt(now);
            token.setLastUsedAt(now);
            token.setRefreshTokenExpiresAt(now.plus(30, ChronoUnit.DAYS));
            tokenRepository.save(token);
            Map<String, String> response = new HashMap<>();
            response.put("access_token", newAccessToken);
            response.put("message", "Token đã được gia hạn thêm 30 ngày");
            response.put("expires_in", "30 ngày");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody() UserDTO userDTO) {
        userService.requestRegister(userDTO);
        return ResponseEntity.ok("Đã gửi OTP đến email của bạn");
    }

    @PostMapping("/verifyOTP")
    public ResponseEntity<User> register(@RequestParam(name = "email") String email, @RequestParam(name = "otp") String otp) {
        return ResponseEntity.ok(userService.register(email, otp));
    }

    @GetMapping("/getInfoUser")
    public ResponseEntity<UserDTO> getInfoUser() {
        return ResponseEntity.ok(userService.getByUser());
    }

    @GetMapping("/getByName/{id}")
    public ResponseEntity<UserDTO> getByName(@PathVariable(name = "id") Long id) {

        return ResponseEntity.ok(userService.findByName(id));
    }
}
