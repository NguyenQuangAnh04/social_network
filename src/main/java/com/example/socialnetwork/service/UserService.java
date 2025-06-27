package com.example.socialnetwork.service;

import com.example.socialnetwork.component.JwtUtils;
import com.example.socialnetwork.dto.PostDTO;
import com.example.socialnetwork.dto.UserDTO;
import com.example.socialnetwork.enums.OTPPurpose;
import com.example.socialnetwork.enums.Role;
import com.example.socialnetwork.model.User;
import com.example.socialnetwork.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class UserService implements IUserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private CurrentUserService currentUserService;
    private final Map<String, UserDTO> account = new ConcurrentHashMap<>();

    @Override
    public String login(String username, String password) {
        Optional<User> user = Optional.ofNullable(userRepository.findByUsername(username).orElseThrow(
                () -> new RuntimeException("Kiểm tra lại tài khoản hoặc mật khẩu")
        ));
        if (!bCryptPasswordEncoder.matches(password, user.get().getPassword()))
            throw new RuntimeException("Kiểm tra lại tài khoản hoặc mật khẩu");
        return jwtUtils.generateToken(user.get());
    }

    @Override
    public User register(String email, String otp) {
        UserDTO userRegisterDTO = account.get(email);
        if (userRegisterDTO == null)
            throw new RuntimeException("Thông tin đăng ký đã hết hạn hoặc không tồn tại");
        if (!OTPService.verifyOTP(email, otp))
            throw new RuntimeException("OTP không chính xác hoặc đã hết hạn");
        Optional<User> existUserName = userRepository.findByUsername(userRegisterDTO.getUsername());
        if (existUserName.isPresent()) throw new RuntimeException("Tài khoản đã tồn tại");
        Optional<User> existEmail = userRepository.findByEmail(email);
        if (existEmail.isPresent()) throw new RuntimeException("Email đã tồn tại");
        User user = new User();
        user.setUsername(userRegisterDTO.getUsername());
        user.setPassword(bCryptPasswordEncoder.encode(userRegisterDTO.getPassword()));
        user.setFirstName(userRegisterDTO.getFirstName());
        user.setRole(Role.USER);
        user.setFullName(userRegisterDTO.getFirstName() + " " + userRegisterDTO.getEmail());
        user.setEmail(userRegisterDTO.getEmail());
        user.setLastName(userRegisterDTO.getLastName());
        user.setCreatedAt(LocalDateTime.now());
        if (userRegisterDTO.getProfilePicture() != null) {
            user.setProfilePicture(userRegisterDTO.getProfilePicture());
        }
        return userRepository.save(user);
    }
    @Override
    public void requestRegister(UserDTO userDTO) {
        if (userRepository.findByUsername(userDTO.getUsername()).isPresent())
            throw new RuntimeException("Tài khoản đã tồn tại");
        if (userRepository.findByEmail(userDTO.getEmail()).isPresent())
            throw new RuntimeException("Email đã tồn tại");

        if (userDTO.getPassword().length() < 8)
            throw new RuntimeException("Mật khẩu phải có 8 kí tự!");
        if (!userDTO.getPassword().equals(userDTO.getConfirmPassword()))
            throw new RuntimeException("Mật khẩu không khớp");
        OTPService.sendOTP(userDTO.getEmail(), OTPPurpose.FOR_REGISTER);
        account.put(userDTO.getEmail(), userDTO);
    }
    @Override
    public UserDTO getByUser() {
        User user = userRepository.findById(currentUserService.getUserCurrent().getId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy user"));
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setFullName(user.getFullName());
        return userDTO;
    }

    @Override
    public UserDTO findByName(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (!user.isPresent()) throw new EntityNotFoundException("Không tìm thấy người dùng");
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.get().getId());
        userDTO.setFullName(user.get().getFullName());
        userDTO.setProfilePicture(user.get().getProfilePicture());
        List<PostDTO> postDTOS = user.get().getPosts().stream().map(item -> {
            PostDTO postDTO = new PostDTO();
            postDTO.setFullName(item.getAuthor().getFirstName() + " " + item.getAuthor().getLastName());
            postDTO.setImage_url(item.getImageUrl());
            postDTO.setContent(item.getContent());
            postDTO.setTimeAgo(getTimeAgo(item.getCreatedAt()));
            return postDTO;
        }).collect(Collectors.toList());
        userDTO.setPostDTOS(postDTOS);
        return userDTO;
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
