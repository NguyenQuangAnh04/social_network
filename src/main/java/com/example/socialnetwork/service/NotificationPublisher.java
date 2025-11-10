package com.example.socialnetwork.service;

import com.example.socialnetwork.model.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationPublisher {
    private final SimpMessagingTemplate messagingTemplate;

    public void push(Notification notification) {
        messagingTemplate.convertAndSend(
                "/topic/notifications." + notification.getReceiverId().getId(), notification
        );
    }
}
