package com.example.socialnetwork.service;

import com.example.socialnetwork.dto.LikeDTO;
import com.example.socialnetwork.model.Like;
import com.example.socialnetwork.model.Post;
import com.example.socialnetwork.model.User;
import com.example.socialnetwork.repository.LikeRepository;
import com.example.socialnetwork.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LikeService implements ILikeService {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private CurrentUserService currentUserService;
    @Override
    public Like likePost(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow();
        User user = currentUserService.getUserCurrent();
        Like like = new Like();
        like.setPost(post);
        like.setUser(user);
        return likeRepository.save(like);
    }

    @Override
    public void unlikePost(Long postId) {
        User currentUser = currentUserService.getUserCurrent();
        Like like = likeRepository.findByUserAndPostId(currentUser, postId).orElseThrow(() -> new EntityNotFoundException("Không tìm thấy like"));
        likeRepository.delete(like);
    }


}
