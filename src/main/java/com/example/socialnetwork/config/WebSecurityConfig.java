package com.example.socialnetwork.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    @Autowired
    private JwtFilter filter;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/oauth2/**", "/login", "/login/**", "/error", "/api/login","/api/logout", "/api/verifyOTP", "/api/register", "/api/refresh-token", "/api/check-auth", "/api/searchByName*").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/logout").hasRole("USER")
                        .requestMatchers(HttpMethod.GET, "/api/like", "/api/like/*").hasAnyRole("USER")
                        .requestMatchers(HttpMethod.POST, "/api/like/comment/*").hasAnyRole("USER")
                        .requestMatchers("/ws/info/*", "/ws/sockjs-node/**", "/ws/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/post/*").hasRole("USER")
                        .requestMatchers(HttpMethod.GET, "/api/*/*", "/api/post/*","/api/post/**", "/api/post").hasRole("USER")
                        .requestMatchers(HttpMethod.GET, "/api/*", "/api").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/comment/*", "/api/like/*", "/api/upload/**", "/api/friend/**", "/api/message/*").hasRole("USER")
                        .requestMatchers(HttpMethod.GET, 
                                "/api/message/*", "/api/message/**").hasRole("USER")
                        .requestMatchers(HttpMethod.GET, "/api/comment/*").hasAnyRole("USER")
                        .requestMatchers(HttpMethod.POST, "/api/follow/*/*").hasAnyRole("USER")
                        .requestMatchers(HttpMethod.DELETE, "/api/follow/*/*").hasAnyRole("USER")
                        .requestMatchers(HttpMethod.DELETE, "/api/like/**", "/api/post/delete/*").hasRole("USER")
                        .requestMatchers("/oauth2/success").permitAll()
                        .requestMatchers("/chat/send").hasRole("USER")
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(oAuth2AuthenticationSuccessHandler)
                        .failureUrl("/login?error")
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(bCryptPasswordEncoder());
        return daoAuthenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(List.of("Set-Cookie"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}