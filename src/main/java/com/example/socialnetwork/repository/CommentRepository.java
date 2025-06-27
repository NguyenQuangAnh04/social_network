package com.example.socialnetwork.repository;

import com.example.socialnetwork.model.Comment;
import com.example.socialnetwork.model.Post;
import com.example.socialnetwork.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllCommentByPost(Post post);
    Comment findByUser(User user);

    List<Comment> findByPost(Post post);
    @Query("SELECT c from Comment c WHERE c.post.id = :postId AND c.parent IS NOT NULL ")
    List<Comment> findChildCommentsByPostId(@Param("postId") Long postId);
    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId AND c.parent IS NULL")
    List<Comment> findParentCommentsByPostId(@Param("postId") Long postId);
    @Modifying
    @Query("DELETE FROM Comment c WHERE c.post.id = :postId AND c.parent IS NOT NULL")
    void deleteChildCommentsByPostId(@Param("postId") Long postId);

    @Modifying
    @Query("DELETE FROM Comment c WHERE c.post.id = :postId AND c.parent IS NULL")
    void deleteParentCommentsByPostId(@Param("postId") Long postId);


}
