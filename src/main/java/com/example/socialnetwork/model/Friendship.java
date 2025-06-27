package com.example.socialnetwork.model;

import com.example.socialnetwork.enums.FriendshipStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Friendship {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    @ManyToOne
    private User sender;
    @ManyToOne
    private User receiver;
    @Enumerated(EnumType.STRING)
    private FriendshipStatus status;
    private LocalDateTime created_at;
}
