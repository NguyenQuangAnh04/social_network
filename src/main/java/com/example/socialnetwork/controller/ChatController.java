package com.example.socialnetwork.controller;

import com.example.socialnetwork.dto.MessageDTO;
import com.example.socialnetwork.dto.NotificationDTO;
import com.example.socialnetwork.dto.ReadDTO;
import com.example.socialnetwork.service.MessageService;
import com.example.socialnetwork.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {
    private final MessageService messageService;
    private final NotificationService notificationService;
    @MessageMapping("/chat")
    public void handleMessage(MessageDTO message) {
        messageService.sendMessage(message);
    }

    @MessageMapping("/read")
    public void markAsRead(ReadDTO readDTO){
        messageService.markMessagesAsRead(readDTO.getUserId(), readDTO.getRoom());
    }
    @MessageMapping("/create-notify")
    public ResponseEntity<NotificationDTO> createNotification(NotificationDTO request
    ) {
        NotificationDTO notification = notificationService.createNotification(
                request.getSenderId(),
                request.getReceiverId(),
                request.getContent(),
                request.getType(),
                request.getPostId(),
                request.getCommentId()
        );
        return ResponseEntity.ok(notification);
    }

}
