package com.example.socialnetwork.service;

import com.example.socialnetwork.dto.CommentDTO;
import com.example.socialnetwork.dto.FriendShipDTO;
import com.example.socialnetwork.dto.LikeDTO;
import com.example.socialnetwork.dto.PostDTO;
import com.example.socialnetwork.model.*;
import com.example.socialnetwork.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
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
    private FriendShipRepository friendShipRepository;

    @Override
    public Post createPost(PostDTO postDTO) {
        User user = currentUserService.getUserCurrent();
        Post post = new Post();
        post.setContent(postDTO.getContent());
        post.setAuthor(user);
        post.setCreatedAt(LocalDateTime.now());

        if (postDTO.getImage_url() != null) {
            post.setImageUrl(postDTO.getImage_url());
        }
        return postRepository.save(post);
    }

    @Override
    public List<PostDTO> findAll() {
        return postRepository.findAll().stream().sorted((p1, p2) -> Long.compare(p2.getId(), p1.getId())).map(item -> {
            PostDTO postDTO = new PostDTO();
            postDTO.setContent(item.getContent());
            postDTO.setId(item.getId());
            postDTO.setUserId(item.getAuthor().getId());
            postDTO.setFullName(item.getAuthor().getFullName());
            postDTO.setTimeAgo(getTimeAgo(item.getCreatedAt()));
            postDTO.setImage_url(item.getImageUrl());
            if (item.getImageUrl() != null) postDTO.setImage_url(item.getImageUrl());
            List<Comment> comments = commentRepository.findAllCommentByPost(item);
            List<CommentDTO> commentDTOS = new ArrayList<>();
            for (Comment comment : comments) {
                CommentDTO commentDTO = new CommentDTO();
                commentDTO.setId(comment.getId());
                commentDTO.setContent(comment.getContent());
                commentDTO.setFullName(comment.getUser().getFirstName() + " " + comment.getUser().getLastName());
                commentDTO.setPostId(comment.getPost().getId());
                commentDTO.setUserId(comment.getUser().getId());
                commentDTO.setTimeAgo(getTimeAgo(comment.getCreatedAt()));
                if (comment.getParent() != null) {
                    commentDTO.setParentComment(comment.getParent().getId());
                }
                commentDTO.setImageUrl(comment.getUser().getProfilePicture());
                commentDTOS.add(commentDTO);
            }
            List<LikeDTO> likeDTOList = new ArrayList<>();
            List<Like> likes = likeRepository.findAllByPost(item);
            for (Like like : likes) {
                LikeDTO likeDTO = new LikeDTO();
                likeDTO.setFullName(like.getUser().getFullName());
                likeDTO.setImageUrl(like.getUser().getProfilePicture());
                likeDTO.setPostId(item.getId());
                likeDTO.setId(item.getId());
                likeDTO.setUserId(item.getAuthor().getId());
                likeDTOList.add(likeDTO);
            }
            Long totalLike = likeRepository.totalLikeByPost(item.getId());
            postDTO.setCommentDTOList(commentDTOS);
            postDTO.setLikeDTOList(likeDTOList);
            postDTO.setTotalLike(totalLike);
            return postDTO;
        }).collect(Collectors.toList());
    }

    @Override
    public List<PostDTO> getAllPostByUser() {
        List<Post> post = postRepository.findByAuthor(currentUserService.getUserCurrent());
        if (post.isEmpty()) throw new EntityNotFoundException("Tài khoản này chưa đăng bài viết nào");
        return post.stream().map(item -> {
            PostDTO postDTO = new PostDTO();
            postDTO.setFullName(item.getAuthor().getFirstName() + " " + item.getAuthor().getLastName());
            postDTO.setImage_url(item.getImageUrl());
            postDTO.setContent(item.getContent());
            postDTO.setTimeAgo(getTimeAgo(item.getCreatedAt()));
            List<Comment> comments = commentRepository.findAllCommentByPost(item);
            List<CommentDTO> commentDTOS = new ArrayList<>();
            for (Comment comment : comments) {
                CommentDTO commentDTO = new CommentDTO();
                commentDTO.setId(comment.getId());
                commentDTO.setContent(comment.getContent());
                commentDTO.setFullName(comment.getUser().getFirstName() + " " + comment.getUser().getLastName());
                commentDTO.setTimeAgo(getTimeAgo(comment.getCreatedAt()));
                commentDTO.setPostId(comment.getPost().getId());
                commentDTO.setUserId(comment.getUser().getId());
                if (comment.getParent() != null) {
                    commentDTO.setParentComment(comment.getParent().getId());
                }
                commentDTO.setImageUrl(comment.getUser().getProfilePicture());
                commentDTOS.add(commentDTO);
            }
            List<LikeDTO> likeDTOList = new ArrayList<>();
            List<Like> likes = likeRepository.findAllByPost(item);
            for (Like like : likes) {
                LikeDTO likeDTO = new LikeDTO();
                likeDTO.setFullName(like.getUser().getFirstName() + " " + like.getUser().getLastName());
                likeDTO.setImageUrl(like.getUser().getProfilePicture());
                likeDTO.setPostId(item.getId());
                likeDTO.setUserId(currentUserService.getUserCurrent().getId());
                likeDTOList.add(likeDTO);
            }
            postDTO.setCommentDTOList(commentDTOS);
            postDTO.setLikeDTOList(likeDTOList);
            return postDTO;
        }).collect(Collectors.toList());
    }

    @Override
    public List<PostDTO> findAllPostByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tài khoản"));

        List<Post> postList = postRepository.findByAuthor(user);


        // Kiểm tra xem người hiện tại có phải bạn với user không (làm 1 lần duy nhất)
        String currentFullName = currentUserService.getUserCurrent().getFullName();
        String otherFullName = user.getFullName();

        Optional<Friendship> friendshipOpt = friendShipRepository
                .findAcceptedFriendshipBetween(currentFullName, otherFullName);

        Long friendId = null;
        if (!userId.equals(currentUserService.getUserCurrent().getId())) {
            if (friendshipOpt.isPresent()) {
                friendId = friendshipOpt.get().getId();
            }
        }
        if (postList.isEmpty()) {
            PostDTO postDTO = new PostDTO();
            postDTO.setFullName(user.getFullName());
            postDTO.setFriendId(friendId);
            postDTO.setCommentDTOList(Collections.emptyList());
            postDTO.setLikeDTOList(Collections.emptyList());
            return List.of(postDTO);
        }
        final Long finalFriendId = friendId;

        return postList.stream().map(item -> {
            PostDTO postDTO = new PostDTO();

            postDTO.setFullName(user.getFullName());
            postDTO.setImage_url(item.getImageUrl());
            postDTO.setContent(item.getContent());
            postDTO.setId(item.getId());
            // Comments
            List<CommentDTO> commentDTOS = commentRepository.findAllCommentByPost(item)
                    .stream().map(comment -> {
                        CommentDTO dto = new CommentDTO();
                        dto.setId(comment.getId());
                        dto.setContent(comment.getContent());
                        dto.setUserId(comment.getUser().getId());
                        dto.setFullName(comment.getUser().getFirstName() + " " + comment.getUser().getLastName());
                        dto.setTimeAgo(getTimeAgo(comment.getCreatedAt()));
                        if (comment.getParent() != null) {
                            dto.setParentComment(comment.getParent().getId());
                        }
                        dto.setImageUrl(comment.getUser().getProfilePicture());
                        return dto;
                    }).collect(Collectors.toList());

            // Likes
            List<LikeDTO> likeDTOS = likeRepository.findAllByPost(item)
                    .stream().map(like -> {
                        LikeDTO dto = new LikeDTO();
                        dto.setFullName(like.getUser().getFirstName() + " " + like.getUser().getLastName());
                        dto.setImageUrl(like.getUser().getProfilePicture());
                        dto.setPostId(item.getId());
                        dto.setUserId(like.getUser().getId());
                        return dto;
                    }).collect(Collectors.toList());

            postDTO.setCommentDTOList(commentDTOS);
            postDTO.setLikeDTOList(likeDTOS);
            postDTO.setFriendId(finalFriendId);

            return postDTO;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deletePostById(Long postId) {
        // Validate
        User currentUser = userRepository.findById(currentUserService.getUserCurrent().getId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người dùng này"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy bài viết"));

        List<Comment> childComments = commentRepository.findChildCommentsByPostId(postId);
        if (!childComments.isEmpty()) {
            commentRepository.deleteChildCommentsByPostId(postId);
            System.out.println("Đã xóa " + childComments.size() + " comment con");
        }

        List<Comment> parentComments = commentRepository.findParentCommentsByPostId(postId);
        if (!parentComments.isEmpty()) {
            commentRepository.deleteParentCommentsByPostId(postId);
            System.out.println("Đã xóa " + parentComments.size() + " comment cha");
        }

        // Xóa likes nếu có
        List<Like> likes = likeRepository.findByPost(post);
        if (!likes.isEmpty()) {
            likeRepository.deleteAll(likes);
            System.out.println("Đã xóa " + likes.size() + " likes");
        }

        postRepository.delete(post);
        System.out.println("Đã xóa bài viết thành công - Post ID: " + postId);
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
