package com.example.socialnetwork.repository;

import com.example.socialnetwork.model.Like;
import com.example.socialnetwork.model.Post;
import com.example.socialnetwork.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByUserAndPostId(User user, Long postId);

    List<Like> findAllByPost(Post post);

    @Query("SELECT COUNT(l) from Like l where l.post.id = :post")
    Long totalLikeByPost(@Param("post") Long post);

    List<Like> findByPost(Post post);
}
