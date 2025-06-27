package com.example.socialnetwork.config;

import com.example.socialnetwork.component.JwtUtils;
import com.example.socialnetwork.model.User;
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

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        String registrationId = ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();
        String email = oAuth2User.getAttribute("email");
        if ("github".equals(registrationId) && (email == null || email.isEmpty())) {
            OAuth2AuthorizedClient client = oAuth2AuthorizedClientService.loadAuthorizedClient(oauthToken.getAuthorizedClientRegistrationId(),
                    oauthToken.getName());
            String accessToken = client.getAccessToken().getTokenValue();
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ResponseEntity<List<Map<String, Object>>> emailResponse = restTemplate.exchange("https://api.github.com/user/emails", HttpMethod.GET,
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
        if (email == null || email.isBlank()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Không lấy được email");
            return;
        }
        Optional<User> user = userRepository.findByEmail(email);
        User finalUser;
        if (user.isPresent()) {
            finalUser = user.get();
        } else {
            User newUser = new User();
            if (registrationId.equals("google")) {
                String googleId =  oAuth2User.getAttribute("sub");
                newUser.setGoogleId(googleId);
                String fullName = oAuth2User.getAttribute("name");
                String tmp = email.split("@")[0];
                newUser.setUsername(tmp);
                newUser.setEmail(email);
                newUser.setFullName(fullName);
            } else if (registrationId.equals("github")) {
                Integer githubId = oAuth2User.getAttribute("id");
                String gitHubName = oAuth2User.getAttribute("login");
                newUser.setFullName(gitHubName);
                newUser.setUsername(gitHubName);
                newUser.setGithubId(githubId);
            }
            newUser.setPassword(UUID.randomUUID().toString());
            finalUser = userRepository.save(newUser);
        }
        String token = jwtUtils.generateToken(finalUser);
        response.sendRedirect("http://localhost:5173/oauth2/redirect?token=" + token);
    }
}