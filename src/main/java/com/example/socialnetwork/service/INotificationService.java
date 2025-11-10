package com.example.socialnetwork.service;

import com.example.socialnetwork.dto.NotificationDTO;
import com.example.socialnetwork.model.Notification;

import java.util.List;

public interface INotificationService {
    Notification createNotification(Long userId, String content,String type);
    List<NotificationDTO> getLatestNotifications(Long userId);
    void markAsRead(Long notificationId);
}
