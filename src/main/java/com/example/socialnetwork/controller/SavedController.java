package com.example.socialnetwork.controller;

import com.example.socialnetwork.dto.SaveDTO;
import com.example.socialnetwork.service.ISaved;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/saved")
@RequiredArgsConstructor
public class SavedController {
    private final ISaved saved;

    @GetMapping("/{userId}")
    public ResponseEntity<List<SaveDTO>> findAll(@PathVariable Long userId) {
        return ResponseEntity.ok(saved.findAllSavedByUser(userId));
    }

    @PostMapping("/save-post/{postId}/{userId}")
    public ResponseEntity<Void> save(@PathVariable Long postId, @PathVariable Long userId) {
        saved.savePost(postId, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete/{postId}")
    public ResponseEntity<Void> delete(@PathVariable Long postId) {
        saved.unSavePost(postId);
        return ResponseEntity.ok().build();
    }
}
