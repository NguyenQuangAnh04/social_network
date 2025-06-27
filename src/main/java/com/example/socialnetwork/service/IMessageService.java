package com.example.socialnetwork.service;

import com.example.socialnetwork.dto.MessageDTO;
import com.example.socialnetwork.model.Message;
import com.example.socialnetwork.model.User;

import java.util.List;

public interface IMessageService {
    Message sendMessage(MessageDTO messageDTO);
    List<MessageDTO> historyMessage(Long userReceiver);

    List<MessageDTO> getAllChatByUser();
}
