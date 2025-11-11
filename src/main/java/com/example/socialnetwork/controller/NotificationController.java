package com.example.socialnetwork.controller;

import com.example.socialnetwork.dto.NotificationDTO;
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

    @GetMapping("/latest/{userId}")
    public ResponseEntity<List<NotificationDTO>> getLatestNotifications(@PathVariable Long userId) {
        List<NotificationDTO> notifications = notificationService.getLatestNotifications(userId);
        return ResponseEntity.ok(notifications);
    }

    @PostMapping("/read")
    public ResponseEntity<Void> markAsRead(@RequestParam Long[] notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok().build();
    }
}
