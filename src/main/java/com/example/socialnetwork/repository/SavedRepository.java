package com.example.socialnetwork.repository;

import com.example.socialnetwork.model.Notification;
import com.example.socialnetwork.model.Post;
import com.example.socialnetwork.model.Saved;
import com.example.socialnetwork.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SavedRepository extends JpaRepository<Saved, Long> {
    List<Saved> findAllByUser(User user);
    @Query("SELECT s.user.id from Saved s where s.post.id = :postId ")
    Long[] findAllByPost(@Param("postId") Long postId);
    Saved findByPost(Post post);


}
