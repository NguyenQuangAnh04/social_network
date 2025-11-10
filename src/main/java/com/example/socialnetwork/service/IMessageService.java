package com.example.socialnetwork.service;

import com.example.socialnetwork.dto.MessageDTO;
import com.example.socialnetwork.model.Message;
import com.example.socialnetwork.model.User;

import java.util.List;

public interface IMessageService {
    MessageDTO sendMessage(MessageDTO messageDTO);
    List<MessageDTO> historyMessage(String room);

    List<MessageDTO> getAllConversations();

    void markMessagesAsRead(Long userId, String roomName);

    int countMessageNotRead(Long userId);
}
