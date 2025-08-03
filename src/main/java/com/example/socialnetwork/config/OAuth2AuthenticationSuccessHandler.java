package com.example.socialnetwork.config;

import com.example.socialnetwork.component.JwtUtils;
import com.example.socialnetwork.model.Token;
import com.example.socialnetwork.model.User;
import com.example.socialnetwork.repository.TokenRepository;
import com.example.socialnetwork.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Component
@Slf4j
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private OAuth2AuthorizedClientService oAuth2AuthorizedClientService;
    @Autowired
    private TokenRepository tokenRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        String registrationId = oauthToken.getAuthorizedClientRegistrationId();
        String email = oAuth2User.getAttribute("email");
        // Nếu Github không trả email
        if ("github".equals(registrationId) && (email == null || email.isEmpty())) {
            OAuth2AuthorizedClient client = oAuth2AuthorizedClientService.loadAuthorizedClient(
                    oauthToken.getAuthorizedClientRegistrationId(),
                    oauthToken.getName()
            );

            String accessToken = client.getAccessToken().getTokenValue();

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ResponseEntity<List<Map<String, Object>>> emailResponse = restTemplate.exchange(
                    "https://api.github.com/user/emails", HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<>() {
                    }
            );

            List<Map<String, Object>> emails = emailResponse.getBody();
            if (emails != null) {
                for (Map<String, Object> e : emails) {
                    if (Boolean.TRUE.equals(e.get("primary")) && Boolean.TRUE.equals(e.get("verified"))) {
                        email = (String) e.get("email");
                        break;
                    }
                }
            }


        }
        if (email == null || email.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Không lấy được email từ provider");
            return;
        }
        Optional<User> user = userRepository.findByEmail(email);
        User finalUser;

        if (user.isPresent()) {
            finalUser = user.get();
        } else {
            User newUser = new User();
            newUser.setEmail(email);
            if ("google".equals(registrationId)) {
                String googleId = oAuth2User.getAttribute("sub");
                newUser.setGoogleId(googleId);
            } else if ("github".equals(registrationId)) {
                Integer githubId = oAuth2User.getAttribute("id");
                newUser.setGithubId(githubId);
            }
            String tmp = email.split("@")[0];
            newUser.setUsername(tmp);
            newUser.setPassword(UUID.randomUUID().toString());
            finalUser = userRepository.save(newUser);

        }
        String accessToken = jwtUtils.generateToken(finalUser);
        ResponseCookie cookie = ResponseCookie.from("access_token", accessToken)
                .path("/")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .maxAge(15 * 60)
                .build();

        Token token = new Token();
        token.setUser(finalUser);
        token.setRefreshToken(UUID.randomUUID().toString());
        token.setRefreshTokenCreatedAt(LocalDateTime.now());
        token.setRefreshTokenExpiresAt(LocalDateTime.now().plus(30, ChronoUnit.DAYS));
        tokenRepository.save(token);

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refresh_token", token.getRefreshToken())
                .httpOnly(true)
                .path("/")
                .secure(true)
                .sameSite("None")
                .maxAge(30 * 24 * 60 * 60)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
        if(finalUser.getFullName() == null || finalUser.getFullName().isEmpty()){
            response.sendRedirect("http://localhost:5173/complete-profile");
            return;
        }
        response.sendRedirect("http://localhost:5173");
    }


}