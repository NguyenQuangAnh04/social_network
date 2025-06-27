package com.example.socialnetwork.service;

import com.example.socialnetwork.dto.FriendShipDTO;
import com.example.socialnetwork.model.Friendship;

import java.util.List;

public interface IFriendService {
    void sendFriendRequest(Long userId);

    void rejectedFriendRequest(Long friendShipId);

    void acceptFriend(Long friendShipId);
    FriendShipDTO findById(Long userId);

    List<FriendShipDTO> findByName(String name);
}
