package com.example.socialnetwork.repository;

import com.example.socialnetwork.model.Comment;
import com.example.socialnetwork.model.Like;
import com.example.socialnetwork.model.Post;
import com.example.socialnetwork.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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

    @Query("SELECT l FROM Like l WHERE l.comment.id = :commentId AND l.user.id = :userId")
    Optional<Like> findByCommentIdAndUserId(
            @Param("commentId") Long commentId,
            @Param("userId") Long userId
    );

    @Query("SELECT count(l) FROM Like l WHERE l.comment.id = :commentId")
    int totalLikeComment(@Param("commentId") Long commentId);


    @Query("SELECT l.comment.id FROM Like l where l.user.id = :userId")
    List<Long> likeByUser(@Param("userId") Long userId);
    @Modifying
    @Query("DELETE FROM Like l WHERE l.comment.id = :commentId")
   void deleteByComment(@Param("commentId") Long commentId);
}


