package com.example.socialnetwork.service;

import com.example.socialnetwork.dto.PostDTO;
import com.example.socialnetwork.dto.SaveDTO;
import com.example.socialnetwork.model.Post;
import com.example.socialnetwork.model.Saved;
import com.example.socialnetwork.model.User;
import com.example.socialnetwork.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SavedService implements ISaved {
    private final SavedRepository savedRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;

    @Override
    public List<SaveDTO> findAllSavedByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Not found userId" + userId));
        List<Saved> saveds = savedRepository.findAllByUser(user);
        return saveds.stream().map(item -> {
            Long totalComment = (long) commentRepository.totalCommentByPost(item.getPost().getId());
            Long totalLike = (long) likeRepository.totalLikeByPost(item.getPost().getId());
            PostDTO postDTO = PostDTO.builder()
                    .content(item.getPost().getContent())
                    .fullName(item.getUser().getFullName())
                    .username(item.getUser().getUsername())
                    .totalLike(totalLike)
                    .totalComment(totalComment)
                    .image_url(item.getPost().getImageUrl())
                    .build();
            return SaveDTO.builder()
                    .id(item.getId())
                    .post(postDTO)
                    .build();
        }).toList();
    }

    @Override
    public void savePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Not found postId" + postId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Not found userId" + userId));
        Saved saved = Saved.builder()
                .post(post)
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();
        savedRepository.save(saved);
    }

    @Override
    public void unSavePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Not found postId" + postId));
        Saved existing = savedRepository.findByPost(post);
        if (existing != null) {
            savedRepository.delete(existing);
        }
    }
}
