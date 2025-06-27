package com.example.socialnetwork.repository;

import com.example.socialnetwork.model.Message;
import com.example.socialnetwork.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    @Query("select m from Message m WHERE " +
            "m.sender.id = :userId1 and m.receiver.id = :userId2 " + " OR " +
            "m.sender.id = :userId2 and m.receiver.id = :userId1 " +
            "order by m.timestamp asc "
    )
    List<Message> findChatHistory(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
    @Query(value = "SELECT u.id, u.first_name, u.last_name FROM users u WHERE u.id IN (SELECT DISTINCT CASE WHEN m.sender_id = :userId THEN m.receiver_id ELSE m.sender_id END FROM messages m WHERE m.sender_id = :userId OR m.receiver_id = :userId)", nativeQuery = true)
    List<Object[]> findConversationPartnerNames(@Param("userId") Long userId);


}
