package com.example.socialnetwork.controller;

import com.example.socialnetwork.dto.MessageDTO;
import com.example.socialnetwork.model.Message;
import com.example.socialnetwork.service.CurrentUserService;
import com.example.socialnetwork.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ChatWebSocketController {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private MessageService messageService;
    @Autowired
    private CurrentUserService currentUserService;
    @MessageMapping("/chat/send")
    public void sendMessage(MessageDTO messageDTO) {
        try {
            System.out.println("📤 WebSocket direct message: " + messageDTO);

            // Lưu vào database
            Message message = messageService.sendMessage(messageDTO);

            // Tạo response DTO
            MessageDTO responseDTO = new MessageDTO();
            responseDTO.setId(message.getId());
            responseDTO.setUserId1(messageDTO.getUserId1());
            responseDTO.setUserId2(messageDTO.getUserId2());
            responseDTO.setContent(messageDTO.getContent());

            // Gửi đến cả 2 users
            messagingTemplate.convertAndSend("/topic/app/" +messageDTO.getUserId1(), responseDTO);
            messagingTemplate.convertAndSend("/topic/app/" + messageDTO.getUserId2(), responseDTO);

            System.out.println("📡 WebSocket broadcasted");

        } catch (Exception e) {
            System.err.println("❌ WebSocket error: " + e.getMessage());
        }
    }
}
