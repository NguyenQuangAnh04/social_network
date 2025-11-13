package com.example.socialnetwork.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.socialnetwork.dto.CommentDTO;
import com.example.socialnetwork.model.Comment;
import com.example.socialnetwork.model.Post;
import com.example.socialnetwork.repository.CommentRepository;
import com.example.socialnetwork.repository.PostRepository;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
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
    public String uploadFilePost(MultipartFile file) throws IOException {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Thumbnails.of(file.getInputStream())
                .size(800, 800)
                .outputQuality(0.8f)
                .toOutputStream(outputStream);
        Map<?,?> uploadResult = cloudinary.uploader().upload(outputStream.toByteArray(),
                ObjectUtils.asMap("resource_type", "auto"));
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
