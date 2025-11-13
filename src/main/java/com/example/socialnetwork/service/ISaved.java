package com.example.socialnetwork.service;

import com.example.socialnetwork.dto.SaveDTO;
import com.example.socialnetwork.model.User;

import java.util.List;

public interface ISaved {
    List<SaveDTO> findAllSavedByUser(Long userId);

    void savePost(Long postId, Long userId);
    void unSavePost(Long postId, Long userId);


}
