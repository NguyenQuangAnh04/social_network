package com.example.socialnetwork.config;

import com.example.socialnetwork.component.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@Slf4j
public class JwtFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private UserDetailsService userDetailsService;
    private static final List<String> EXCLUDE_URLS = List.of(
            "/api/login", "/api/register", "/ws-chat", "/api/logout", "/ws-chat/**");

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
//            if (request.getServletPath().startsWith("/login") || request.getServletPath().startsWith("/.well-known")||request.getServletPath().startsWith("/ws") || EXCLUDE_URLS.contains(request.getServletPath())) {
//                filterChain.doFilter(request, response);
//                return;
//            }
            if (request.getServletPath().startsWith("/api/refresh-token")) {
                filterChain.doFilter(request, response);
                return;
            }

            String authHeader = request.getHeader("Authorization");
            String token = null;
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
            }else{
                if(request.getCookies() != null){
                    for(Cookie cookie : request.getCookies()){
                        if(cookie.getName().equals("access_token")){
                            token = cookie.getValue();
                        }
                    }
                }
            }
            if (token == null) {
                filterChain.doFilter(request, response);
                return;
            }
            String username = jwtUtils.extractUserName(token);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails user = userDetailsService.loadUserByUsername(username);
                if (jwtUtils.validate(token, user)) {
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(user,
                            null,
                            user.getAuthorities());
                    usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            }
            filterChain.doFilter(request, response);
        } catch (Exception e) {
//            log.error("Error in JWT filter: {}", e.getMessage(), e);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }

    }
}
