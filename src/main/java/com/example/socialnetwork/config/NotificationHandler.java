package com.example.socialnetwork.config;

import com.example.socialnetwork.service.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class NotificationHandler extends TextWebSocketHandler {

    private final Map<Long, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final CurrentUserService currentUserService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Long userId = currentUserService.getUserCurrent().getId();
        sessions.put(userId, session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
    }

    public void sendNotification(Long userId, String content){
        WebSocketSession session = sessions.get(userId);

        if(session != null && session.isOpen()){
            try{
                session.sendMessage(new TextMessage(content));
            }catch (IOException ex){
                sessions.remove(userId);
            }
        }
    }
}
