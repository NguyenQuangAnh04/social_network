package com.example.socialnetwork.controller;

import com.example.socialnetwork.component.JwtUtils;
import com.example.socialnetwork.dto.CompleteProfileRequest;
import com.example.socialnetwork.dto.UserDTO;
import com.example.socialnetwork.model.Token;
import com.example.socialnetwork.model.User;
import com.example.socialnetwork.repository.TokenRepository;
import com.example.socialnetwork.repository.UserRepository;
import com.example.socialnetwork.service.CurrentUserService;
import com.example.socialnetwork.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

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
    @Autowired
    private CurrentUserService currentUserService;
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody() UserDTO userDTO) {
        Optional<User> user = userRepository.findByUsername(userDTO.getUsername());
        if (user.isEmpty()) throw new EntityNotFoundException("Kiểm tra lại tài khoản hoặc mật khẩu");
        String access_token = userService.login(userDTO.getUsername(), userDTO.getPassword());
        String refreshToken = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now(ZoneId.systemDefault());
        Token token = Token.builder().token(access_token).refreshToken(refreshToken).user(user.get()).tokenCreatedAt(now).refreshTokenCreatedAt(now).refreshTokenIssuedAt(now).lastUsedAt(now).refreshTokenExpiresAt(now.plus(30, ChronoUnit.DAYS)).build();
        tokenRepository.save(token);
        ResponseCookie accessTokenCookie = ResponseCookie.from("access_token", access_token) // Tên cookie + giá trị
                .httpOnly(true)  // Cookie này chỉ được gửi qua HTTP(S), JavaScript KHÔNG thể đọc được → chống XSS
                .secure(true)    // Chỉ gửi cookie qua HTTPS → không gửi qua HTTP thường (bắt buộc dùng khi SameSite=None)
                .path("/")       // Cookie được gửi kèm MỌI request trong domain, ví dụ: /api/posts, /api/refresh-token đều gửi
                .sameSite("None") // Chỉ gửi cookie nếu request đến từ cùng site → chống CSRF. Nếu FE/BE khác domain cần dùng "None"
                .maxAge(15 * 60) // Cookie tồn tại 15 phút (đơn vị: giây)
                .build();

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("None")
                .maxAge(30 * 24 * 60 * 60)
                .build();

        return ResponseEntity.ok()
                .headers(headers -> {
                    headers.add(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
                    headers.add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
                }).body("Success");
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@CookieValue(name = "refresh_token", required = false) String refreshTokenFromCookie,
                                          @RequestBody(required = false) Map<String, String> request) {
        try {
            String refreshToken = refreshTokenFromCookie;
//            if (refreshToken != null && request != null) {
//                refreshToken = request.get("refresh_token");
//            }

            if (refreshToken == null || refreshToken.trim().isEmpty()) {
                throw new IllegalArgumentException("Refresh token not empty");
            }
            Optional<Token> tokenOptional = tokenRepository.findByRefreshToken(refreshToken);
            if (tokenOptional.isEmpty()) throw new EntityNotFoundException("Token không hợp lệ");
            Token token = tokenOptional.get();
            LocalDateTime now = LocalDateTime.now(ZoneId.systemDefault());
            if (token.getRefreshTokenExpiresAt().isAfter(now)) {
                String newAccessToken = jwtUtils.generateToken(token.getUser());
//                token.setToken(newAccessToken);
//                token.setTokenCreatedAt(now);
//                token.setLastUsedAt(now);
                ResponseCookie accessTokenCookie = ResponseCookie.from("access_token", newAccessToken)
                        .httpOnly(true)
                        .secure(true)
                        .maxAge(15 * 60)
                        .path("/")
                        .sameSite("None")
                        .build();

                return ResponseEntity.ok()
                        .header("Set-Cookie", accessTokenCookie.toString())
                        .body(Map.of("message", "Đã làm mới token thành công!"));
            } else {
                LocalDateTime thirtyDayAgo = now.minusDays(30);
                if (token.getLastUsedAt().isAfter(thirtyDayAgo)) {
                    String newAccessToken = jwtUtils.generateToken(token.getUser());
//                    token.setToken(newAccessToken);
                    token.setRefreshTokenExpiresAt(now.plus(30, ChronoUnit.DAYS));
//                    token.setTokenCreatedAt(now);
                    ResponseCookie accessTokenCookie = ResponseCookie.from("access_token", newAccessToken)
                            .httpOnly(true)
                            .secure(true)
                            .sameSite("Strict")
                            .path("/")
                            .maxAge(15 * 60)
                            .build();
                    ResponseCookie refreshTokenCookie = ResponseCookie.from("refresh_token", refreshToken)
                            .httpOnly(true)
                            .secure(false)
                            .sameSite("Strict")
                            .path("/")
                            .maxAge(30 * 24 * 60 * 60)
                            .build();
                    return ResponseEntity.ok()
                            .header("Set-Cookie", accessTokenCookie.toString())
                            .header("Set-Cookie", refreshTokenCookie.toString())
                            .body(Map.of("message", "Access & Refresh token đã được gia hạn thêm 30 ngày!"));

                } else {
                    tokenRepository.delete(token);
                    throw new RuntimeException("Refresh token đã hết hạn. Vui lòng đăng nhập lại.");
                }
            }
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

//    @GetMapping("/getByName/{id}")
//    public ResponseEntity<UserDTO> getByName(@PathVariable(name = "id") Long id) {
//
//        return ResponseEntity.ok(userService.findByName(id));
//    }

    @PostMapping("/logout")
    @Transactional
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("refresh_token")) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }
        if (refreshToken != null) {
            tokenRepository.deleteByRefreshToken(refreshToken);
        }
        ResponseCookie clearAccessToken = ResponseCookie.from("access_token", "")
                .path("/")
                .httpOnly(true)
                .secure(true)
                .maxAge(0)
                .sameSite("None")
                .build();

        ResponseCookie clearRefreshToken = ResponseCookie.from("refresh_token", "")
                .path("/")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .maxAge(0)
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, clearAccessToken.toString())
                .header(HttpHeaders.SET_COOKIE, clearRefreshToken.toString())
                .body("Logout");
    }

    @GetMapping("/check-auth")
    public ResponseEntity<?> checkAuth(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No cookie");
        }

        String accessToken = null;
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("access_token")) {
                accessToken = cookie.getValue();
            }
        }

        if (accessToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No access token");
        }

       if(jwtUtils.validateToken(accessToken)){
           String username = jwtUtils.extractUserName(accessToken);
           return ResponseEntity.ok(Map.of("username", username));
       }else{
           return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");

       }
    }
    @PostMapping("/complete-profile")
    public ResponseEntity<?> completeProfile(@RequestBody() CompleteProfileRequest completeProfileRequest) {
        User user = currentUserService.getUserCurrent();
        if(user == null) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (user.getFullName() != null && !user.getFullName().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Profile đã hoàn thiện rồi");
        }

        user.setFullName(completeProfileRequest.getFullName());
        return ResponseEntity.ok(userRepository.save(user));
    }

    @GetMapping("/searchByName")
    public ResponseEntity<List<UserDTO>> searchByName(@RequestParam(name = "name") String name) {
        return ResponseEntity.ok(userService.findByName(name));
    }

    @GetMapping("")
    public ResponseEntity<List<UserDTO>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }
}
