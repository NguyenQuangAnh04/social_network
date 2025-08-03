package com.example.socialnetwork.service;

import com.example.socialnetwork.dto.MessageDTO;
import com.example.socialnetwork.model.Message;
import com.example.socialnetwork.model.User;
import com.example.socialnetwork.repository.MessageRepository;
import com.example.socialnetwork.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@Slf4j
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
        message.setSender(userSend);
        message.setReceiver(userReceiver);
        message.setContent(messageDTO.getContent());
        message.setTimestamp(LocalDateTime.now());
        return messageRepository.save(message);
    }

    @Override
    public List<MessageDTO> historyMessage(Long userId) {
        List<Message> messages = messageRepository.findByAllContent(currentUserService.getUserCurrent().getId(), userId);
        if(messages.isEmpty()) throw new EntityNotFoundException("This chat not message");
        return messages.stream().map(item -> {
            MessageDTO messageDTO = new MessageDTO();
            messageDTO.setContent(item.getContent());
            messageDTO.setId(item.getId());
            messageDTO.setUserId1(item.getSender().getId());
            messageDTO.setUserId2(item.getReceiver().getId());
            messageDTO.setTimeStamp(item.getTimestamp().toString());
            messageDTO.setSenderId(item.getSender().getId());
            return messageDTO;
        }).collect(Collectors.toList());
    }

    @Override
    public List<MessageDTO> getAllChatByUser() {
        List<Long> findByChatFromId = messageRepository.findConversationPartnerIds(currentUserService.getUserCurrent().getId());
        return findByChatFromId.stream().map(user -> {
            Optional<User> userEntity = userRepository.findById(user);
            if(userEntity.isEmpty()) throw new EntityNotFoundException("Not found userId " + userEntity.get().getId());
            PageRequest pageRequest = PageRequest.of(0, 1);
            String findByLastContent = messageRepository.findContentLast(currentUserService.getUserCurrent().getId(), userEntity.get().getId(), pageRequest).stream().findFirst().orElse(null);
            MessageDTO messageDTO = new MessageDTO();
            messageDTO.setId(userEntity.get().getId());
            messageDTO.setFullName(userEntity.get().getFullName());
            messageDTO.setContent(findByLastContent.toString());
            return messageDTO;
        }).collect(Collectors.toList());
    }


    public String getTimeAgo(LocalDateTime localDateTime) {
        Duration duration = Duration.between(localDateTime, LocalDateTime.now());
        long seconds = duration.getSeconds();
        if (seconds < 60) return "Vừa xong";
        if (seconds < 3600) return seconds / 60 + " phút trước";
        if (seconds < 86400) return seconds / 3600 + " giờ trước";
        if (seconds < 2592000) return seconds / 86400 + " ngày trước";
        if (seconds < 31104000) return seconds / 2592000 + " tháng trước";
        return seconds / 31104000 + " năm trước";
    }


}
