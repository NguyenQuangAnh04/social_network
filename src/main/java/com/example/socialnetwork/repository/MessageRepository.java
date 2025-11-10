package com.example.socialnetwork.repository;

import com.example.socialnetwork.model.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT m FROM Message m WHERE m.id IN (" +
            "SELECT MAX(m2.id) FROM Message m2 " +
            "WHERE (m2.sender.id = :userId OR m2.receiver.id = :userId) " +
            "GROUP BY m2.room.id) " +
            "ORDER BY m.timestamp DESC")
    List<Message> findMessagesByUser(@Param("userId") Long userId);
    @Query("SELECT m.content FROM Message m WHERE m.room = :room ORDER BY m.timestamp DESC")
    List<String> findContentLast(@Param("room") String room);

    @Query("SELECT m FROM Message m WHERE m.room.id = :id")
    List<Message> findByRoomId(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query("update  Message m set m.isRead = true where m.room.name = :room and m.receiver.id = :userId")
    void markAsRead(@Param("userId") Long userId, @Param("room") String room);
    @Query("SELECT count(m.id) from Message m where m.receiver.id = :userId and m.isRead = false")
    int countByNotRead(@Param("userId") Long userId);

}
