package com.example.socialnetwork.service;

import com.example.socialnetwork.dto.FriendDTO;
import com.example.socialnetwork.dto.UserDTO;
import com.example.socialnetwork.model.User;
import jakarta.mail.MessagingException;

import java.util.List;

public interface IUserService {
    String login(String username, String password);
    User register(String email, String otp);
    void requestRegister(UserDTO userDTO) throws MessagingException;

    UserDTO getByUser();
    UserDTO findByUsername(String username);
    List<UserDTO> findByName(String fullname);
    List<UserDTO> findAll();

}
