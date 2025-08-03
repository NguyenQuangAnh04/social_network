package com.example.socialnetwork.service;

import com.example.socialnetwork.dto.LikeResponseDTO;
import com.example.socialnetwork.model.Comment;
import com.example.socialnetwork.model.Like;
import com.example.socialnetwork.model.Post;
import com.example.socialnetwork.model.User;
import com.example.socialnetwork.repository.CommentRepository;
import com.example.socialnetwork.repository.LikeRepository;
import com.example.socialnetwork.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LikeService implements ILikeService {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private CurrentUserService currentUserService;
    @Autowired
    private CommentRepository commentRepository;

    @Override
    public Like likePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post không tồn tại"));

        User user = currentUserService.getUserCurrent();
        if (user == null) {
            throw new RuntimeException("Chưa đăng nhập");
        }

        if (likeRepository.findByUserAndPostId(user, postId).isPresent()) {
            throw new RuntimeException("Đã like post này rồi");
        }
        Like like = new Like();
        like.setPost(post);
        like.setUser(user);
        like.setCreatedAt(LocalDateTime.now());
        return likeRepository.save(like);
    }

    @Override
    public void unlikePost(Long postId) {
        User currentUser = currentUserService.getUserCurrent();
        Like like = likeRepository.findByUserAndPostId(currentUser, postId).orElseThrow(() -> new EntityNotFoundException("Không tìm thấy like"));
        likeRepository.delete(like);
    }

    @Override
    @Transactional
    public Like likeComment(Long commentId) {
        Optional<Comment> comment = commentRepository.findById(commentId);
        if (comment.isEmpty()) throw new EntityNotFoundException("Not found comment " + commentId);
        Optional<Like> hasLiked = likeRepository.findByCommentIdAndUserId(commentId, currentUserService.getUserCurrent().getId());
        if (hasLiked.isPresent()) {
            likeRepository.delete(hasLiked.get());
            return null;
        } else {
            Like like = new Like();
            like.setCreatedAt(LocalDateTime.now());
            like.setComment(comment.get());
            like.setUser(currentUserService.getUserCurrent());
            return likeRepository.save(like);
        }
    }



    @Override
    public List<Long> findHasLikedByUser() {
       return likeRepository.likeByUser(currentUserService.getUserCurrent().getId())
               .stream().filter(Objects::nonNull).collect(Collectors.toList());
    }
}
