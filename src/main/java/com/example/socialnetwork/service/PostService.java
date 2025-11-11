package com.example.socialnetwork.service;

import com.example.socialnetwork.dto.LikeDTO;
import com.example.socialnetwork.dto.PostDTO;
import com.example.socialnetwork.dto.UserDTO;
import com.example.socialnetwork.dto.UserProfileResponse;
import com.example.socialnetwork.mapper.PostMapper;
import com.example.socialnetwork.model.*;
import com.example.socialnetwork.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PostService implements IPostService {

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LikeRepository likeRepository;
    @Autowired
    private CurrentUserService currentUserService;
    @Autowired
    private CloudinaryService cloudinaryService;
    @Autowired
    private PostMapper postMapper;
    @Autowired
    private FollowRepository followRepository;
    @Autowired
    private SavedRepository savedRepository;

    @Override
    public Post createPost(PostDTO postDTO, MultipartFile file) throws IOException {
        User user = currentUserService.getUserCurrent();
        Post post = postMapper.toEntity(postDTO, user);
        if (file != null) {
            String url = cloudinaryService.uploadFilePost(file, post.getId());
            post.setImageUrl(url);
        }
        return postRepository.save(post);
    }

    @Override
    public List<PostDTO> findAll() {
        return postRepository.findAll().stream()
                .sorted((p1, p2) -> Long.compare(p2.getId(), p1.getId()))
                .map(item -> {
                    PostDTO postDTO = new PostDTO();
                    postDTO.setId(item.getId());
                    postDTO.setUserId(item.getAuthor().getId());
                    postDTO.setFullName(item.getAuthor().getFullName());
                    postDTO.setUserId(item.getAuthor().getId());
                    postDTO.setUsername(item.getAuthor().getUsername());
                    postDTO.setContent(item.getContent());
                    postDTO.setImage_url(item.getImageUrl());
                    postDTO.setTimeAgo(getTimeAgo(item.getCreatedAt()));

                    Long[] saves = savedRepository.findAllByPost(item.getId());
                    if(saves != null) {
                        postDTO.setSavedByUser(saves);
                    }
                    List<Like> likeEntity = likeRepository.findAllByPost(item);
                    List<LikeDTO> likes = likeEntity.stream().map(like -> {
                        LikeDTO likeDTO = new LikeDTO();
                        likeDTO.setId(like.getId());
                        likeDTO.setPostId(item.getId());
                        likeDTO.setUserId(like.getUser().getId());
                        return likeDTO;
                    }).collect(Collectors.toList());
                    postDTO.setLikeDTOList(likes);
                    Long totalComment = (long) commentRepository.totalCommentByPost(item.getId());
                    Long totalLike = (long) likeRepository.totalLikeByPost(item.getId());
                    postDTO.setTotalLike(totalLike);
                    postDTO.setTotalComment(totalComment);
                    return postDTO;
                })
                .collect(Collectors.toList());
    }

    @Override
    public UserProfileResponse getAllPostByUser() {
        List<Post> post = postRepository.findByAuthor(currentUserService.getUserCurrent());

        if (post.isEmpty()) throw new EntityNotFoundException("Tài khoản này chưa đăng bài viết nào");
        List<PostDTO> posts = post.stream().sorted((p1, p2) -> Long.compare(p2.getId(), p1.getId())).map(item -> {
            PostDTO postDTO = new PostDTO();
            postDTO.setId(item.getId());
            postDTO.setUserId(item.getAuthor().getId());
            postDTO.setFullName(item.getAuthor().getFullName());
            postDTO.setContent(item.getContent());
            postDTO.setImage_url(item.getImageUrl());
            postDTO.setTimeAgo(getTimeAgo(item.getCreatedAt()));
            List<Like> likeEntity = likeRepository.findAllByPost(item);
            List<LikeDTO> likes = likeEntity.stream().map(like -> {
                LikeDTO likeDTO = new LikeDTO();
                likeDTO.setId(like.getId());
                likeDTO.setUserId(like.getUser().getId());
                return likeDTO;
            }).collect(Collectors.toList());
            postDTO.setLikeDTOList(likes);
            Long totalComment = (long) commentRepository.totalCommentByPost(item.getId());
            Long totalLike = (long) likeRepository.totalLikeByPost(item.getId());
            postDTO.setTotalLike(totalLike);
            postDTO.setTotalComment(totalComment);
            return postDTO;
        }).collect(Collectors.toList());
        UserDTO userDTO = new UserDTO();
        userDTO.setId(currentUserService.getUserCurrent().getId());
        userDTO.setFullName(currentUserService.getUserCurrent().getFullName());
        userDTO.setUsername(currentUserService.getUserCurrent().getUsername());
        UserProfileResponse response = new UserProfileResponse();
        response.setUser(userDTO);
        response.setPosts(posts);
        return response;

    }

    @Override
    public UserProfileResponse findAllPostByUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tài khoản"));
        List<Post> postList = postRepository.findByAuthor(user);
        if (postList.isEmpty()) {
            UserDTO userDTO = new UserDTO();
            userDTO.setId(user.getId());
            userDTO.setFullName(user.getFullName());
            Optional<Follow> follow = followRepository.findByFollowerIdAndFollowingId(user.getId(), currentUserService.getUserCurrent().getId());
            if (follow.isPresent()) {
                userDTO.setIsFollowing(Boolean.TRUE);
            } else {
                userDTO.setIsFollowing(Boolean.FALSE);
            }
            userDTO.setUsername(user.getUsername());
            UserProfileResponse response = new UserProfileResponse();
            response.setUser(userDTO);
            response.setPosts(new ArrayList<>());
            return response;
        }
        List<PostDTO> posts = postList.stream().sorted((p1, p2) -> Long.compare(p2.getId(), p1.getId())).map(item -> {
            PostDTO postDTO = new PostDTO();
            postDTO.setId(item.getId());
            postDTO.setFullName(item.getAuthor().getFullName());
            postDTO.setContent(item.getContent());
            postDTO.setImage_url(item.getImageUrl());
            postDTO.setUserId(item.getAuthor().getId());
            postDTO.setTimeAgo(getTimeAgo(item.getCreatedAt()));
            List<Like> likeEntity = likeRepository.findAllByPost(item);
            List<LikeDTO> likes = likeEntity.stream().map(like -> {
                LikeDTO likeDTO = new LikeDTO();
                likeDTO.setId(like.getId());
                likeDTO.setUserId(like.getUser().getId());
                return likeDTO;
            }).collect(Collectors.toList());
            postDTO.setLikeDTOList(likes);
            Long totalComment = (long) commentRepository.totalCommentByPost(item.getId());
            Long totalLike = (long) likeRepository.totalLikeByPost(item.getId());
            postDTO.setTotalLike(totalLike);
            postDTO.setTotalComment(totalComment);
            return postDTO;
        }).toList();
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setFullName(user.getFullName());
        Optional<Follow> follow = followRepository.findByFollowerIdAndFollowingId(currentUserService.getUserCurrent().getId(), user.getId());
        if (follow.isPresent()) {
            userDTO.setIsFollowing(Boolean.TRUE);
        } else {
            userDTO.setIsFollowing(Boolean.FALSE);
        }
        userDTO.setUsername(user.getUsername());
        UserProfileResponse response = new UserProfileResponse();
        response.setUser(userDTO);
        response.setPosts(posts);
        return response;
    }

    @Override
    public PostDTO editPost(PostDTO postDTO) {
        Optional<Post> post = postRepository.findById(postDTO.getId());
        if (post.isEmpty()) throw new EntityNotFoundException("Not found post");
        post.get().setContent(postDTO.getContent());
        post.get().setUpdatedAt(LocalDateTime.now());
        postRepository.save(post.get());
        return postDTO;
    }

    @Override
    @Transactional
    public void deletePostById(Long postId) {
        // Validate
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy bài viết"));

        List<Comment> childComments = commentRepository.findChildCommentsByPostId(postId);
        if (!childComments.isEmpty()) {
            for (Comment comment : childComments) {
                likeRepository.deleteByComment(comment.getId());
            }
        }
        if (!childComments.isEmpty()) {
            for (Comment comment : childComments) {
                commentRepository.deleteChildComments(comment.getId());
            }
        }

        List<Comment> parentComments = commentRepository.findParentCommentsByPostId(postId);
        for (Comment comment : parentComments) {
            likeRepository.deleteByComment(comment.getId());
        }
        if (!parentComments.isEmpty()) {
            for (Comment comment : parentComments) {
                commentRepository.deleteParentCommentsByPostId(comment.getPost().getId());
            }
        }

        // Xóa likes nếu có
        List<Like> likes = likeRepository.findByPost(post);
        if (!likes.isEmpty()) {
            likeRepository.deleteAll(likes);
        }
        postRepository.delete(post);
    }

    @Override
    public PostDTO findPostById(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));
        Long totalComment = (long) commentRepository.totalCommentByPost(post.getId());
        Long totalLike = (long) likeRepository.totalLikeByPost(post.getId());
        return PostDTO.builder()
                .id(postId)
                .content(post.getContent())
                .timeAgo(getTimeAgo(post.getCreatedAt()))
                .totalLike(totalLike)
                .image_url(post.getImageUrl())
                .fullName(post.getAuthor().getUsername())
                .build();

    }

    private String getTimeAgo(LocalDateTime localDateTime) {
        // Tính khoảng thời gian giữa thời điểm 'localDateTime' (thời gian tạo bài viết) và thời điểm hiện tại
        Duration duration = Duration.between(localDateTime, LocalDateTime.now());
        long seconds = duration.getSeconds();
        if (seconds < 60) return "Vừa xong";
        if (seconds < 3600) return seconds / 60 + " phút trước";
        if (seconds < 86400) return seconds / 3600 + " giờ trước";
        if (seconds < 2592000) return seconds / 86400 + " ngày trước";
        if (seconds < 31104000) return seconds / 2592000 + " tháng trước";
        return seconds / 31104000 + " năm trước";
    }
}
