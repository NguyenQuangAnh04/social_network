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
            Message message = messageService.sendMessage(messageDTO);
            MessageDTO responseDTO = new MessageDTO();
            responseDTO.setId(message.getId());
            responseDTO.setUserId1(messageDTO.getUserId1());
            responseDTO.setUserId2(messageDTO.getUserId2());
            responseDTO.setContent(messageDTO.getContent());
            responseDTO.setTimeStamp(String.valueOf(message.getTimestamp()));
            responseDTO.setSenderId(message.getSender().getId());
            messagingTemplate.convertAndSend("/topic/app/" + responseDTO.getUserId1(), responseDTO);
            messagingTemplate.convertAndSend("/topic/app/" + responseDTO.getUserId2(), responseDTO);
        } catch (Exception e) {
            System.err.println(" WebSocket error: " + e.getMessage());
        }
    }
}
