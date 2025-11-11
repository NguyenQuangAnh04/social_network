package com.example.socialnetwork.service;

import com.example.socialnetwork.dto.NotificationDTO;
import com.example.socialnetwork.model.Notification;

import java.util.List;

public interface INotificationService {
    NotificationDTO createNotification(Long userId,Long receiverId, String content,String type, Long postId, Long commentId);
    List<NotificationDTO> getLatestNotifications(Long userId);
    void markAsRead(Long[] notificationId);
}
