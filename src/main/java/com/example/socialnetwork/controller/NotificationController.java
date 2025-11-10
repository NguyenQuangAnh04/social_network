package com.example.socialnetwork.controller;

import com.example.socialnetwork.dto.NotificationDTO;
import com.example.socialnetwork.dto.NotificationRequest;
import com.example.socialnetwork.model.Notification;
import com.example.socialnetwork.model.User;
import com.example.socialnetwork.service.CurrentUserService;
import com.example.socialnetwork.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;
    private final CurrentUserService currentUserService;
   @PostMapping("/create")
   public ResponseEntity<Notification> createNotification(
           @RequestBody NotificationRequest request
           ) {
       Notification notification = notificationService.createNotification(request.getReceiverId(), request.getContent(),request.getType());
       return ResponseEntity.ok(notification);
   }
    @GetMapping("/latest")
    public ResponseEntity<List<NotificationDTO>> getLatestNotifications() {
        User currentUser = currentUserService.getUserCurrent();
        List<NotificationDTO> notifications = notificationService.getLatestNotifications(currentUser.getId());
        return ResponseEntity.ok(notifications);
    }

    @PostMapping("/read/{notificationId}")
    public ResponseEntity<Void> markAsRead(@PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok().build();
    }
}
