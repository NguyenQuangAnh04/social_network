package com.example.socialnetwork.controller;

import com.example.socialnetwork.dto.MessageDTO;
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
    public ResponseEntity<MessageDTO> sendMessage(@RequestBody() MessageDTO messageDTO) {
        return ResponseEntity.ok(messageService.sendMessage(messageDTO));
    }

    @GetMapping("/history-message/{room}")
    public ResponseEntity<List<MessageDTO>> historyMessage(@PathVariable(name = "room") String room) {
        return ResponseEntity.ok(messageService.historyMessage(room));
    }

    @GetMapping("/count-message/{userId}")
    public ResponseEntity<Integer> countMessageNotRead(@PathVariable Long userId) {
        return ResponseEntity.ok(messageService.countMessageNotRead(userId));
    }


    @GetMapping("/conversations")
    public ResponseEntity<List<MessageDTO>> getConversations() {
        return ResponseEntity.ok(messageService.getAllConversations());
    }


}
