package com.example.socialnetwork.repository;

import com.example.socialnetwork.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    @Query("SELECT n FROM Notification n WHERE n.receiverId.id = :receiverId ORDER BY n.createdAt DESC")
    List<Notification> findByReceiverIdOrderByCreatedAtDesc(@Param("receiverId") Long receiverId);

    @Query("UPDATE Notification  n set n.isRead = true  where n.id = :id ")
    void markAsRead(@Param("id") Long id);
}