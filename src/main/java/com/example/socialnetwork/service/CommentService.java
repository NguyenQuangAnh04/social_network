package com.example.socialnetwork.service;

import com.example.socialnetwork.dto.CommentDTO;
import com.example.socialnetwork.model.Comment;
import com.example.socialnetwork.model.Post;
import com.example.socialnetwork.model.User;
import com.example.socialnetwork.repository.CommentRepository;
import com.example.socialnetwork.repository.LikeRepository;
import com.example.socialnetwork.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CommentService implements ICommentService {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CurrentUserService currentUserService;
    @Autowired
    private LikeRepository likeRepository;
    @Override
    public Comment createComment(CommentDTO commentDTO) {
        User user = currentUserService.getUserCurrent();
        Post post = postRepository.findById(commentDTO.getPostId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy bài viết"));
        Comment newComment = new Comment();
        newComment.setContent(commentDTO.getContent());
        newComment.setCreatedAt(LocalDateTime.now());
        if (commentDTO.getImageUrl() != null) newComment.setImageUrl(commentDTO.getImageUrl());
        newComment.setPost(post);
        newComment.setUser(user);
        return commentRepository.save(newComment);
    }

    @Override
    public List<CommentDTO> showRepLyCommentParent(Long parentId, Long postId) {
        List<Comment> comment = commentRepository.findByParentAndPostId(parentId, postId);
        if (comment.isEmpty()) throw new EntityNotFoundException("Comment này chưa có relies nào!");
        return comment.stream().map(item -> {
            CommentDTO commentDTO = new CommentDTO();
            commentDTO.setId(item.getId());
            commentDTO.setFullName(item.getUser().getFullName());
            commentDTO.setUserId(item.getUser().getId());
            commentDTO.setParentComment(item.getParent().getId());
            commentDTO.setContent(item.getContent());
            return commentDTO;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Comment replyComment(CommentDTO commentDTO) {
        log.info("Id comment " + commentDTO.getId() + "content " + commentDTO.getContent());
        User user = currentUserService.getUserCurrent();
        Comment comment = commentRepository.findById(commentDTO.getId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy comment gốc"));
        Post post = postRepository.findById(commentDTO.getPostId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy bài viết"));
        Comment replyComment = new Comment();
        replyComment.setParent(comment);
        replyComment.setContent(commentDTO.getContent());
        replyComment.setCreatedAt(LocalDateTime.now());
        if (commentDTO.getImageUrl() != null) comment.setImageUrl(commentDTO.getImageUrl());
        replyComment.setUser(user);
        replyComment.setPost(post);
        return commentRepository.save(replyComment);
    }

    @Override
    public List<CommentDTO> findAllByPost(Long id) {
        Optional<Post> post = postRepository.findById(id);
        List<Comment> comments = commentRepository.findAllCommentByPost(post.get());
        Map<Long, CommentDTO> commentMap = new HashMap<>();
        List<CommentDTO> roots = new ArrayList<>();
        for (Comment comment : comments) {
            CommentDTO commentDTO = new CommentDTO();
            List<Long> commentList = likeRepository.findByIdx(comment.getId());
            commentDTO.setId(comment.getId());
            commentDTO.setPostId(comment.getPost().getId());
            commentDTO.setFullName(comment.getUser().getFullName());
            commentDTO.setChildren(new ArrayList<>());
            commentDTO.setUserId(comment.getUser().getId());
            commentDTO.setTimeAgo(getTimeAgo(comment.getCreatedAt()));
            commentDTO.setContent(comment.getContent());
            commentDTO.setUsers(commentList);
            commentDTO.setParentComment(comment.getParent() != null ? comment.getParent().getId() : null);
            int total = commentRepository.countParentId(comment.getId());
            int totalLike = likeRepository.totalLikeComment(comment.getId());
            commentDTO.setTotalLike((long) totalLike);
            commentDTO.setTotalComment((long) total);
            commentMap.put(comment.getId(), commentDTO);
        }
        for (CommentDTO dto : commentMap.values()) {
            if (dto.getParentComment() == null) {
                roots.add(dto);
            } else {
                CommentDTO parent = commentMap.get(dto.getParentComment());
                if (parent != null) {
                    parent.getChildren().add(dto);
                }
            }
        }
        return roots;
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
