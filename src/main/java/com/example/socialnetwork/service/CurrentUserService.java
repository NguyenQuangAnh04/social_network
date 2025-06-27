package com.example.socialnetwork.service;

import com.example.socialnetwork.model.User;
import com.example.socialnetwork.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserService {
    @Autowired
    private UserRepository userRepository;

    public User getUserCurrent(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy user"));
    }
}
