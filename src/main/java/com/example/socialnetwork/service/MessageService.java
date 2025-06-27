package com.example.socialnetwork.service;

import com.example.socialnetwork.dto.MessageDTO;
import com.example.socialnetwork.model.Message;
import com.example.socialnetwork.model.User;
import com.example.socialnetwork.repository.MessageRepository;
import com.example.socialnetwork.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class MessageService implements IMessageService {
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CurrentUserService currentUserService;
    @Override
    @Transactional
    public Message sendMessage(MessageDTO messageDTO) {
        User userSend = userRepository.findById(messageDTO.getUserId1())
                .orElseThrow();
        User userReceiver = userRepository.findById(messageDTO.getUserId2())
                .orElseThrow();
        Message message = new Message();
        if (messageDTO.getContent() == null) throw new RuntimeException("Vui lòng nhập tin nhắn để gửi");
        message.setContent(messageDTO.getContent());
        message.setSender(userSend);
        message.setReceiver(userReceiver);
        message.setTimestamp(LocalDateTime.now());
        return messageRepository.save(message);
    }

    @Override
    public List<MessageDTO> historyMessage(Long userReceiver) {
        List<Message> messages = messageRepository.findChatHistory(currentUserService.getUserCurrent().getId(), userReceiver);
        return messages.stream().map(item -> {
            MessageDTO messageDTO = new MessageDTO();
            messageDTO.setContent(item.getContent());
            messageDTO.setId(item.getId());
            messageDTO.setUserId1(item.getSender().getId());
            messageDTO.setUserId2(item.getReceiver().getId());
            messageDTO.setTimeStamp(getTimeAgo(item.getTimestamp()));
            return messageDTO;
        }).collect(Collectors.toList());
    }
    @Override
    public List<MessageDTO> getAllChatByUser() {
        List<Object[]> users = messageRepository.findConversationPartnerNames(currentUserService.getUserCurrent().getId());

        if (users.isEmpty()) {
            throw new EntityNotFoundException("Người dùng này chưa có cuộc trò chuyện");
        }

        return users.stream().map(item -> {
            MessageDTO messageDTO = new MessageDTO();
            String firstName = (String) item[1];
            String lastName = (String) item[2];
            messageDTO.setFullName(firstName + " " + lastName);
            messageDTO.setId((Long) item[0]);
            return messageDTO;
        }).collect(Collectors.toList());
    }


    public String getTimeAgo(LocalDateTime localDateTime){
        Duration duration = Duration.between(localDateTime, LocalDateTime.now());
        long seconds = duration.getSeconds();
        if (seconds < 60) return "Vừa xong";
        if(seconds < 3600) return seconds / 60 + " phút trước";
        if (seconds < 86400) return seconds / 3600 + " giờ trước";
        if (seconds < 2592000) return seconds / 86400 + " ngày trước";
        if (seconds < 31104000) return seconds / 2592000 + " tháng trước";
        return seconds / 31104000 + " năm trước";
    }


}
