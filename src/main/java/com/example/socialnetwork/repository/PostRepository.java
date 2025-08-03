package com.example.socialnetwork.repository;

import com.example.socialnetwork.model.Post;
import com.example.socialnetwork.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("SELECT p from Post p where p.author = :user order by p.createdAt desc ")
    List<Post> findByAuthor(@Param("user") User user);

    @Query("SELECT COUNT(p) FROM Post p WHERE p.author.id = :userId")
    int countPost(@Param("userId") Long userId);


}
