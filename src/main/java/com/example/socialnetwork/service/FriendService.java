package com.example.socialnetwork.service;

import com.example.socialnetwork.dto.FriendShipDTO;
import com.example.socialnetwork.enums.FriendshipStatus;
import com.example.socialnetwork.model.Friendship;
import com.example.socialnetwork.model.Message;
import com.example.socialnetwork.model.User;
import com.example.socialnetwork.repository.FriendShipRepository;
import com.example.socialnetwork.repository.MessageRepository;
import com.example.socialnetwork.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FriendService implements IFriendService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FriendShipRepository friendShipRepository;
    @Autowired
    private CurrentUserService currentUserService;
    @Autowired
    private MessageRepository messageRepository;
    @Override
    public void sendFriendRequest(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tên người dùng"));
        Friendship friendship = new Friendship();
        friendship.setSender(currentUserService.getUserCurrent());
        friendship.setReceiver(user);
        friendship.setStatus(FriendshipStatus.PENDING);
        friendShipRepository.save(friendship);
    }
    @Override
    public void rejectedFriendRequest(Long friendShipId) {
        Friendship friendship = friendShipRepository.findById(friendShipId).orElseThrow();
        friendship.setStatus(FriendshipStatus.REJECTED);
        friendShipRepository.save(friendship);
    }
    @Override
    public void acceptFriend(Long friendShipId) {
        Friendship friendship = friendShipRepository.findById(friendShipId).orElseThrow();
        friendship.setStatus(FriendshipStatus.ACCEPTED);
        friendShipRepository.save(friendship);
        Message message = new Message();
        message.setSender(friendship.getSender());
        message.setReceiver(friendship.getReceiver());
        String senderName = friendship.getReceiver().getFullName();
        message.setContent("Chào " + senderName + "! Mình rất vui khi chúng ta là bạn.");
        message.setTimestamp(LocalDateTime.now());
        messageRepository.save(message);
    }



    @Override
    public FriendShipDTO findById(Long userId) {
        User current = currentUserService.getUserCurrent();
        User target = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người dùng"));

        if (current.getId().equals(target.getId())) {
            throw new IllegalArgumentException("Không thể xem trạng thái với chính mình");
        }
        Friendship friendship = friendShipRepository.findByUsers(current.getId(), target.getId());
        FriendShipDTO dto = new FriendShipDTO();
        dto.setId(target.getId());
        dto.setFullName(target.getFullName());
        dto.setStatus(friendship != null ? friendship.getStatus().name() : "NONE");
        return dto;
    }

    @Override
    public List<FriendShipDTO> findByName(String name) {
        List<User> user = userRepository.findByName(name);
        if(user.isEmpty()) throw new EntityNotFoundException("Không tìm thấy kết quả phù hợp");
        return user.stream().map(item -> {
            FriendShipDTO friendShipDTO = new FriendShipDTO();
            friendShipDTO.setId(item.getId());
            friendShipDTO.setFullName(item.getFullName());
            friendShipDTO.setImageUrl(item.getProfilePicture());
            return friendShipDTO;
        }).collect(Collectors.toList());

    }

}
