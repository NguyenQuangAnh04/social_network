package com.example.socialnetwork.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.socialnetwork.dto.CommentDTO;
import com.example.socialnetwork.model.Comment;
import com.example.socialnetwork.model.Post;
import com.example.socialnetwork.repository.CommentRepository;
import com.example.socialnetwork.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {
    @Autowired
    private Cloudinary cloudinary;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private CurrentUserService currentUserService;
    public String uploadFilePost(MultipartFile file, Long postId) throws IOException {
        Post post = postRepository.findById(postId).orElseThrow(()
                -> new RuntimeException("Không tìm thấy id post"));
        Map<?,?> uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap("resource_type", "auto"));
        post.setImageUrl(uploadResult.get("secure_url").toString());
        postRepository.save(post);
        return uploadResult.get("secure_url").toString();
    }
    public String uploadFileComment(MultipartFile file, Long postId) throws IOException {
        Post post = postRepository.findById(postId).orElseThrow(()
                -> new RuntimeException("Không tìm thấy id post"));
        Map<?,?> uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap("resource_type", "auto"));
        post.setImageUrl(uploadResult.get("secure_url").toString());
        postRepository.save(post);
        return uploadResult.get("secure_url").toString();
    }
}
