package com.example.socialnetwork.controller;

import com.example.socialnetwork.dto.CommentDTO;
import com.example.socialnetwork.model.Comment;
import com.example.socialnetwork.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comment")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @PostMapping("/create")
    public ResponseEntity<Comment> createComment(@RequestBody() CommentDTO commentDTO) {
        return ResponseEntity.ok(commentService.createComment(commentDTO));
    }

    @PostMapping("/reply")
    public ResponseEntity<Comment> replyComment(@RequestBody() CommentDTO commentDTO) {
        return ResponseEntity.ok(commentService.replyComment(commentDTO));
    }
    @GetMapping("/{postId}")
    public ResponseEntity<List<CommentDTO>> showReplyComment(@PathVariable(name = "postId") Long postId) {
        return ResponseEntity.ok(commentService.findAllByPost(postId));
    }
//    @GetMapping("/show-reply-comment/{parentId}/{postId}")
//    public ResponseEntity<List<CommentDTO>> showReplyComment(@PathVariable(name = "parentId") Long parentId,
//                                                             @PathVariable(name = "postId") Long postId) {
//        return ResponseEntity.ok(commentService.showRepLyCommentParent(parentId, postId));
//    }
}
