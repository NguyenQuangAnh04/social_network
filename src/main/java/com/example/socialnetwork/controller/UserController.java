package com.example.socialnetwork.controller;

import com.example.socialnetwork.component.JwtUtils;
import com.example.socialnetwork.dto.CompleteProfileRequest;
import com.example.socialnetwork.dto.UserDTO;
import com.example.socialnetwork.model.Token;
import com.example.socialnetwork.model.User;
import com.example.socialnetwork.repository.TokenRepository;
import com.example.socialnetwork.repository.UserRepository;
import com.example.socialnetwork.service.CurrentUserService;
import com.example.socialnetwork.service.TokenBlackListService;
import com.example.socialnetwork.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api")
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final JwtUtils jwtUtils;
    private final CurrentUserService currentUserService;
    private final StringRedisTemplate stringRedisTemplate;
    private final TokenBlackListService blackListService;
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody() UserDTO userDTO, HttpServletResponse response) {
        Optional<User> user = userRepository.findByUsername(userDTO.getUsername());
        if (user.isEmpty()) throw new EntityNotFoundException("Kiểm tra lại tài khoản hoặc mật khẩu");
        String access_token = userService.login(userDTO.getUsername(), userDTO.getPassword());
        String refreshToken = null;
        LocalDateTime now = LocalDateTime.now(ZoneId.systemDefault());
        Token tokens = tokenRepository.findRefreshTokensByUserId(user.get().getId());
        if (tokens == null ) {
            Token token = Token
                    .builder()
                    .refreshToken(UUID.randomUUID().toString())
                    .user(user.get())
                    .refreshTokenCreatedAt(now)
                    .refreshTokenExpiresAt(now.plusMinutes(30))
                    .build();
            tokenRepository.save(token);
            refreshToken = token.getRefreshToken();
        }else{
            refreshToken = tokens.getRefreshToken();
        }
        Cookie cookie = new Cookie("refresh_token", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setSecure(true);
        cookie.setMaxAge(7 * 24 * 60 * 60);
        response.addCookie(cookie);
        Map<String, String> map = new HashMap<>();
        map.put("access_token", access_token);
        return ResponseEntity.ok(map);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@CookieValue(name = "refresh_token", required = false) String refreshTokenFromCookie,
                                          @RequestBody(required = false) Map<String, String> request) {
        if (refreshTokenFromCookie == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Optional<Token> refreshToken = tokenRepository.findByRefreshToken(refreshTokenFromCookie);
        if (refreshToken.isPresent() && refreshToken.get().getRefreshTokenExpiresAt().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Map<String, String> map = new HashMap<>();
        String token = jwtUtils.generateToken(currentUserService.getUserCurrent());

        map.put("access_token", token);
        return ResponseEntity.ok(map);
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
        String token = request.getHeader("Authorization");
        String authHeader = token.substring(7);
        blackListService.addTokenToBlackList(authHeader);

        Optional<Token> existingToken = tokenRepository.findByRefreshToken(refreshToken);
        existingToken.ifPresent(tokens -> tokenRepository.delete(existingToken.get()));
        ResponseCookie clearRefreshToken = ResponseCookie.from("refresh_token", "")
                .path("/")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, clearRefreshToken.toString())
                .body("Logout");
    }

    @GetMapping("/check-auth")
    public ResponseEntity<?> checkAuth(HttpServletRequest request) {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/complete-profile")
    public ResponseEntity<?> completeProfile(@RequestBody() CompleteProfileRequest completeProfileRequest) {
        User user = currentUserService.getUserCurrent();
        if (user == null) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
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

    @GetMapping("/getInfor")
    public ResponseEntity<UserDTO> search(@RequestParam(name = "username") String username) {
        return ResponseEntity.ok(userService.findByUsername(username));
    }
    @GetMapping("")
    public ResponseEntity<List<UserDTO>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }
}
