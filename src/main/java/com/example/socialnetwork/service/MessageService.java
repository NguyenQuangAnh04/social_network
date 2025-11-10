package com.example.socialnetwork.service;

import com.example.socialnetwork.dto.MessageDTO;
import com.example.socialnetwork.model.Message;
import com.example.socialnetwork.model.Room;
import com.example.socialnetwork.model.User;
import com.example.socialnetwork.repository.MessageRepository;
import com.example.socialnetwork.repository.RoomRepository;
import com.example.socialnetwork.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
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
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    @Autowired
    private RoomRepository roomRepository;

    @Override
    @Transactional
    public MessageDTO sendMessage(MessageDTO messageDTO) {
        User sender = userRepository.findById(messageDTO.getSenderId())
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        User receiver = userRepository.findById(messageDTO.getReceiverId())
                .orElseThrow(() -> new RuntimeException("Receiver not found"));
        if (messageDTO.getContent() == null || messageDTO.getContent().isBlank()) {
            throw new RuntimeException("Vui lòng nhập nội dung tin nhắn");
        }
        Message message = new Message();
        if (messageDTO.getContent() == null) throw new RuntimeException("Vui lòng nhập tin nhắn để gửi");
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(messageDTO.getContent());
        message.setTimestamp(LocalDateTime.now());

        if (messageDTO.getRoom() == null || "undefined".equals(messageDTO.getRoom())) {
            Room room = new Room();
            room.setName(generateRoom(sender.getId(), receiver.getId()));
            roomRepository.save(room);
            message.setRoom(room);
        } else {
            Room existing = roomRepository.findByName(messageDTO.getRoom())
                    .orElseThrow(() -> new IllegalArgumentException("Not found!"));
            message.setRoom(existing);
        }
        Message savedMessage = messageRepository.save(message);
        int countmessage = countMessageNotRead(receiver.getId());
        MessageDTO dto = new MessageDTO().builder()
                .id(savedMessage.getId())
                .content(savedMessage.getContent())
                .receiverId(receiver.getId())
                .senderId(sender.getId())
                .countMessageNotRead(countmessage)
                .room(savedMessage.getRoom().getName())
                .timeStamp(savedMessage.getTimestamp().toString())
                .build();
        log.info("Data: {}", dto);
        simpMessagingTemplate.convertAndSend("/topic/room/" + savedMessage.getRoom().getName(), dto);
        return dto;
    }

    @Override
    public List<MessageDTO> historyMessage(String room) {
        Room existing = roomRepository.findByName(room)
                .orElseThrow(() -> new IllegalArgumentException("Not found"));
        List<Message> messages = messageRepository.findByRoomId(existing.getId());

        if (messages.isEmpty())
            throw new EntityNotFoundException("No messages found in this room");

        Long currentId = currentUserService.getUserCurrent().getId();

        return messages.stream().map(item -> {
            MessageDTO dto = new MessageDTO();
            dto.setId(item.getId());
            dto.setSenderId(item.getSender().getId());
            dto.setReceiverId(item.getReceiver().getId());
            dto.setContent(item.getContent());
            dto.setTimeStamp(item.getTimestamp().toString());
            dto.setParent(item.getSender().getId().equals(currentId));
            dto.setRoom(item.getRoom().getName());
            return dto;
        }).collect(Collectors.toList());
    }


    public List<MessageDTO> getAllConversations() {
        User user = currentUserService.getUserCurrent();
        List<Message> messages = messageRepository.findMessagesByUser(user.getId());

        Map<Long, Message> lastMessageMap = new HashMap<>();

        for (Message m : messages) {
            Long partnerId = m.getSender().getId().equals(user.getId()) ? m.getReceiver().getId() : m.getSender().getId();

            if (!lastMessageMap.containsKey(partnerId) ||
                    m.getTimestamp().isAfter(lastMessageMap.get(partnerId).getTimestamp())) {
                lastMessageMap.put(partnerId, m);
            }
        }

        List<MessageDTO> dtos = new ArrayList<>();
        for (Map.Entry<Long, Message> entry : lastMessageMap.entrySet()) {
            Message m = entry.getValue();
            Long partnerId = entry.getKey();

            User partner = m.getSender().getId().equals(user.getId())
                    ? m.getReceiver()
                    : m.getSender();

            MessageDTO dto = new MessageDTO();
            dto.setId(m.getId());
            dto.setFullName(partner.getFullName());
            dto.setRoom(m.getRoom().getName());
            dto.setReceiverId(partnerId);
            dto.setSenderId(m.getSender().getId());
            dto.setContent(m.getContent());
            dto.setTimeStamp(m.getTimestamp().toString());
            dto.setParent(m.getSender().getId().equals(user.getId()));

            dtos.add(dto);
        }


        return dtos;
    }

    @Override
    @Transactional
    public void markMessagesAsRead(Long userId, String roomName) {
        Room room = roomRepository.findByName(roomName)
                .orElseThrow(() -> new IllegalArgumentException("Not found with room " + roomName));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Not found userId " + userId));

        messageRepository.markAsRead(user.getId(), room.getName());
        messageRepository.flush(); // commit ngay

        int countNotRead = countMessageNotRead(userId);
        simpMessagingTemplate.convertAndSend("/topic/message/count", countNotRead);
    }


    @Override
    public int countMessageNotRead(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Not found userId"));
        return messageRepository.countByNotRead(user.getId());
    }

    public String generateRoom(Long u1, Long u2) {
        Long min = Math.min(u1, u2);
        Long max = Math.max(u1, u2);
        return "room_" + min + "_" + max;
    }


}
