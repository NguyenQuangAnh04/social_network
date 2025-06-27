package com.example.socialnetwork.controller;

import com.example.socialnetwork.dto.FriendShipDTO;
import com.example.socialnetwork.service.CurrentUserService;
import com.example.socialnetwork.service.FriendService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/friend")
@Slf4j
public class FriendShipController {
    @Autowired
    private FriendService friendService;
    @Autowired
    private CurrentUserService currentUserService;

    @PostMapping("/sender/{id}")
    public ResponseEntity<String> senderRequest(@PathVariable(name = "id") Long id) {
        friendService.sendFriendRequest(id);
        return ResponseEntity.ok("Gửi lời mời kết bạn thành công");
    }

    @PostMapping("/rejected/{id}")
    public ResponseEntity<String> rejectedRequest(@PathVariable(name = "id") Long id) {
        friendService.rejectedFriendRequest(id);
        return ResponseEntity.ok("Từ chối kết bạn thành công");
    }

    @PostMapping("/accepted/{id}")
    public ResponseEntity<String> accepted(@PathVariable(name = "id") Long id) {
        friendService.acceptFriend(id);
        return ResponseEntity.ok("Chấp nhận kết bạn thành công");
    }

    @GetMapping("/status/{userId}")
    public ResponseEntity<FriendShipDTO> getFriendStatus(@PathVariable(name = "userId") Long userId){
        return ResponseEntity.ok(friendService.findById(userId));
    }

    @GetMapping("/findByName")
    public ResponseEntity<List<FriendShipDTO>> findByName(@RequestParam(name = "name") String name){
        String decodeName = URLDecoder.decode(name, StandardCharsets.UTF_8);
        log.info("Người dùng " + currentUserService.getUserCurrent().getFullName() + " vừa gõ :" + decodeName);
        return ResponseEntity.ok(friendService.findByName(decodeName));
    }
}
