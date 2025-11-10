package com.example.socialnetwork.config;

import com.example.socialnetwork.component.JwtUtils;
import com.example.socialnetwork.dto.StatusDTO;
import com.example.socialnetwork.service.RedisOnlineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.security.Principal;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {
    private final RedisOnlineService redisOnlineService;
    private final SimpMessagingTemplate messagingTemplate;
    private final JwtUtils jwtUtils;

    @EventListener
    public void onSubscribe(SessionSubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Map<String, Object> sessionAttrs = accessor.getSessionAttributes();
        if (sessionAttrs != null) {
            Long userId = (Long) sessionAttrs.get("userId");
            if (userId != null) {
                redisOnlineService.setUserOnline(userId);
                messagingTemplate.convertAndSend("/topic/status", new StatusDTO(userId, true));
                log.info("User online (subscribe): {}", userId);
            }
        }
    }


    @EventListener
    public void onDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Map<String, Object> sessionAttrs = accessor.getSessionAttributes();
        if (sessionAttrs != null) {
            Long userId = (Long) sessionAttrs.get("userId");
            if (userId != null) {
                redisOnlineService.setUserOffline(userId);
                messagingTemplate.convertAndSend("/topic/status", new StatusDTO(userId, false));
                log.info("User offline (subscribe): {}", userId);
            }
        }


    }
}
