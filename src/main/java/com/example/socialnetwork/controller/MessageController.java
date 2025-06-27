package com.example.socialnetwork.controller;

import com.example.socialnetwork.dto.MessageDTO;
import com.example.socialnetwork.model.Message;
import com.example.socialnetwork.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/message")
public class MessageController {
    @Autowired
    private MessageService messageService;

    @PostMapping("/send-message")
    public ResponseEntity<Message> sendMessage(@RequestBody() MessageDTO messageDTO) {
        return ResponseEntity.ok(messageService.sendMessage(messageDTO));
    }

    @GetMapping("/history-message/{userId}")
    public ResponseEntity<List<MessageDTO>> historyMessage(@PathVariable(name = "userId") Long userId) {
        return ResponseEntity.ok(messageService.historyMessage(userId));
    }

    @GetMapping("/conversations")
    public ResponseEntity<List<MessageDTO>> getConversations() {
        return ResponseEntity.ok(messageService.getAllChatByUser());
    }
}
