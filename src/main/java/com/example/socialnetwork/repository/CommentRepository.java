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
    @Query("SELECT c from Comment c WHERE c.post.id = :postId AND c.parent IS NOT NULL ORDER BY c.id DESC")
    List<Comment> findChildCommentsByPostId(@Param("postId") Long postId);
    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId AND c.parent IS NULL ORDER BY c.id DESC")
    List<Comment> findParentCommentsByPostId(@Param("postId") Long postId);
    @Modifying
    @Query("DELETE FROM Comment c WHERE c.id = :commentId")
    void deleteChildComments(@Param("commentId") Long commentId);
    @Query("SELECT c FROM Comment c where c.parent.id = :parentId AND c.post.id = :postId")
    List<Comment> findByParentAndPostId(@Param("parentId") Long parentId,@Param("postId") Long postId);
    @Modifying
    @Query("DELETE FROM Comment c WHERE c.post.id = :postId AND c.parent IS NULL")
    void deleteParentCommentsByPostId(@Param("postId") Long postId);
    @Query("SELECT COUNT(c) FROM Comment c WHERE  c.parent.id = :parentId")
    int countParentId(@Param("parentId") Long parentId);
    @Query("SELECT COUNT(c) FROM Comment c where  c.post.id = :postId")
    int totalCommentByPost(@Param("postId") Long postId);

}
