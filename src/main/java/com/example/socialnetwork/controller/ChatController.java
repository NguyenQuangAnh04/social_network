package com.example.socialnetwork.controller;

import com.example.socialnetwork.dto.MessageDTO;
import com.example.socialnetwork.dto.ReadDTO;
import com.example.socialnetwork.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {
    private final MessageService messageService;

    @MessageMapping("/chat")
    public void handleMessage(MessageDTO message) {
        messageService.sendMessage(message);
    }

    @MessageMapping("/read")
    public void markAsRead(ReadDTO readDTO){
        messageService.markMessagesAsRead(readDTO.getUserId(), readDTO.getRoom());
    }


}
