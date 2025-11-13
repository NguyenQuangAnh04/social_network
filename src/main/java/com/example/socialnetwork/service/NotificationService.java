package com.example.socialnetwork.service;

import com.example.socialnetwork.dto.NotificationDTO;
import com.example.socialnetwork.model.Comment;
import com.example.socialnetwork.model.Notification;
import com.example.socialnetwork.model.Post;
import com.example.socialnetwork.model.User;
import com.example.socialnetwork.repository.CommentRepository;
import com.example.socialnetwork.repository.NotificationRepository;
import com.example.socialnetwork.repository.PostRepository;
import com.example.socialnetwork.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService implements INotificationService {
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationPublisher notificationPublisher;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final SimpMessagingTemplate template;

    @Override
    public NotificationDTO createNotification(Long senderId, Long receiverId, String content, String type, Long postId, Long commentId) {
        Post existingPost = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        Comment existingComment = null;
        if (commentId != null) {
            existingComment = commentRepository.findById(commentId)
                    .orElseThrow(() -> new RuntimeException("Comment not found"));
        }

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("Not found user id " + senderId));
        User userReceiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("Not found user id " + receiverId));
        Notification notification = Notification.builder()
                .content(content)
                .isRead(false)
                .senderId(sender)
                .type(type)
                .post(existingPost)
                .comment(commentId != null ? existingComment : null)
                .receiverId(userReceiver)
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);
        NotificationDTO notificationDTO = NotificationDTO.builder()
                .content(notification.getContent())
                .senderId(notification.getSenderId().getId())
                .type(notification.getType())
                .postId(notification.getPost().getId())
                .commentId(notification.getComment() != null ? notification.getComment().getId() : null)
                .receiverId(notification.getReceiverId().getId())
                .id(notification.getId())
                .isRead(notification.getIsRead())
                .createdAt(getTimeAgo(notification.getCreatedAt()))
                .fullName(notification.getSenderId().getFullName())
                .build();
        template.convertAndSend("/topic/notifications." + notification.getReceiverId().getId(), notificationDTO);
        return notificationDTO;
    }

    @Override
    public List<NotificationDTO> getLatestNotifications(Long userId) {
        List<Notification> notifications = notificationRepository.findByReceiverIdOrderByCreatedAtDesc(userId);
        return notifications.stream().map(
                item -> {
                    return NotificationDTO.builder()
                            .content(item.getContent())
                            .createdAt(getTimeAgo(item.getCreatedAt()))
                            .isRead(item.getIsRead())
                            .postId(item.getPost().getId())
                            .fullName(item.getSenderId().getFullName())
                            .id(item.getId())
                            .avatar(item.getSenderId().getProfilePicture())
                            .type(item.getType())
                            .build();
                }
        ).toList();
    }

    @Override
    public void markAsRead(Long[] notificationId) {
        for (Long id : notificationId) {
            notificationRepository.markAsRead(id);
        }

    }

    private String getTimeAgo(LocalDateTime localDateTime) {
        // Tính khoảng thời gian giữa thời điểm 'localDateTime' (thời gian tạo bài viết) và thời điểm hiện tại
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
