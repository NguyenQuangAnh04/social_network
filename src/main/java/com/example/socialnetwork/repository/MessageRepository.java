package com.example.socialnetwork.repository;

import com.example.socialnetwork.model.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    //    @Query("SELECT u.id, u.firstName, u.lastName FROM User u " +
//            "WHERE u.id IN " +
//            "(SELECT DISTINCT CASE WHEN m.sender.id = :userId THEN m.receiver.id ELSE m.sender.id END " +
//            "FROM Message m " +
//            "WHERE m.sender.id = :userId OR m.receiver.id = :userId)")
//    List<Object[]> findConversationPartnerNames(@Param("userId") Long userId);
    @Query("SELECT DISTINCT CASE " +
            "WHEN m.sender.id = :userId THEN m.receiver.id " +
            "ELSE m.sender.id " +
            "END " +
            "FROM Message m " +
            "WHERE m.sender.id = :userId OR  m.receiver.id = :userId")
    List<Long> findConversationPartnerIds(@Param("userId") Long userId);

    @Query("SELECT m.content FROM Message m " +
            "WHERE (m.sender.id = :userId AND m.receiver.id = :userId2) " +
            "OR (m.sender.id = :userId2 AND m.receiver.id = :userId) " +
            "ORDER BY m.timestamp DESC")
    List<String> findContentLast(@Param("userId") Long userId, @Param("userId2") Long userId2, Pageable pageable);
    @Query("SELECT m FROM Message m WHERE (m.sender.id = :userId AND m.receiver.id = :userId2) " +
            "OR (m.sender.id = :userId2 AND m.receiver.id = :userId) " )
    List<Message> findByAllContent(@Param("userId") Long userId, @Param("userId2") Long userId2);
}
