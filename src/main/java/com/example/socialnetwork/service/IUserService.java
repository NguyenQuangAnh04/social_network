package com.example.socialnetwork.service;

import com.example.socialnetwork.dto.UserDTO;
import com.example.socialnetwork.model.User;
import jakarta.mail.MessagingException;

public interface IUserService {
    String login(String username, String password);
    User register(String email, String otp);
    void requestRegister(UserDTO userDTO) throws MessagingException;

    UserDTO getByUser();

    UserDTO findByName(Long id);
}
