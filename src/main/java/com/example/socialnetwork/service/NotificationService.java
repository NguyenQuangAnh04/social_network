package com.example.socialnetwork.service;

import com.example.socialnetwork.config.NotificationHandler;
import com.example.socialnetwork.dto.NotificationDTO;
import com.example.socialnetwork.model.Notification;
import com.example.socialnetwork.model.User;
import com.example.socialnetwork.repository.NotificationRepository;
import com.example.socialnetwork.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService implements INotificationService {
    private final CurrentUserService currentUserService;
    private final UserRepository userRepository;
    private final NotificationHandler notificationHandler;
    private final NotificationRepository notificationRepository;
    private final NotificationPublisher notificationPublisher;

    @Override
    public Notification createNotification(Long userId, String content, String type) {
        User user = currentUserService.getUserCurrent();
        User userReceiver = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Not found user id " + userId));
        notificationHandler.sendNotification(userReceiver.getId(), content);
        Notification notification = Notification.builder()
                .content(content)
                .isRead(false)
                .senderId(user)
                .type(type)
                .receiverId(userReceiver)
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);
//        try {
//            String json = new ObjectMapper().writeValueAsString(notification);
//            notificationHandler.sendNotification(userReceiver.getId(), json);
//
//        } catch (JsonProcessingException ex) {
//            ex.printStackTrace();
//        }
        notificationPublisher.push(notification);
        return notification;
    }

    @Override
    public List<NotificationDTO> getLatestNotifications(Long userId) {
        List<Notification> notifications = notificationRepository.findByReceiverIdOrderByCreatedAtDesc(userId);
        return notifications.stream().map(
                item -> {
                    return NotificationDTO.builder()
                            .content(item.getContent())
                            .createdAt(item.getCreatedAt())
                            .sender(item.getSenderId().getFullName())
                            .type(item.getType())
                            .build();
                }
        ).toList();
    }

    @Override
    public void markAsRead(Long notificationId) {
        notificationRepository.markAsRead(notificationId);
    }
}
