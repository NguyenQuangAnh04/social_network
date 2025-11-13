package com.example.socialnetwork.service;

import com.example.socialnetwork.component.JwtUtils;
import com.example.socialnetwork.config.RedisConfig;
import com.example.socialnetwork.dto.FriendDTO;
import com.example.socialnetwork.dto.UserDTO;
import com.example.socialnetwork.enums.OTPPurpose;
import com.example.socialnetwork.enums.Role;
import com.example.socialnetwork.model.Follow;
import com.example.socialnetwork.model.User;
import com.example.socialnetwork.repository.FollowRepository;
import com.example.socialnetwork.repository.RoleRepository;
import com.example.socialnetwork.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
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
    @Autowired
    private FollowRepository followRepository;
    private final StringRedisTemplate stringRedisTemplate;
    @Autowired
    private CloudinaryService cloudinaryService;
    @Autowired
    private RoleRepository roleRepository;

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

        Optional<User> existEmail = userRepository.findByEmail(email);
        if (existEmail.isPresent()) throw new RuntimeException("Email đã tồn tại");
        User user = new User();

        user.setUsername(userRegisterDTO.getUsername());
        user.setPassword(bCryptPasswordEncoder.encode(userRegisterDTO.getPassword()));
        user.setEmail(userRegisterDTO.getEmail());
        user.setFullName(userRegisterDTO.getFullName());
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

//        OTPService.sendOTP(userDTO.getEmail(), OTPPurpose.FOR_REGISTER);
//        account.put(userDTO.getEmail(), userDTO);
        com.example.socialnetwork.model.Role role = roleRepository.findByRole(Role.USER);
        User user = new User();
        user.setEmail(userDTO.getEmail());
        user.setFullName(userDTO.getFullName());
        user.setUsername(userDTO.getUsername());
        user.setRoles(Collections.singleton(role));
        String hashPassword = bCryptPasswordEncoder.encode(userDTO.getPassword());
        user.setPassword(hashPassword);
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @Override
    public UserDTO getByUser() {
        User user = userRepository.findById(currentUserService.getUserCurrent().getId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy user"));
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setFullName(user.getFullName());
        userDTO.setBio(user.getBio());
        userDTO.setUrlSocial(user.getUrlSocial());
        userDTO.setEmailContact(user.getEmailContact());
        userDTO.setProfilePicture(user.getProfilePicture());
        userDTO.setAddress(user.getAddress());
        userDTO.setUsername(user.getUsername());
        return userDTO;
    }

    @Override
    public UserDTO findByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Not found with username" + username));
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setFullName(user.getFullName());
        userDTO.setUrlSocial(user.getUrlSocial());
        userDTO.setEmailContact(user.getEmailContact());
        userDTO.setProfilePicture(user.getProfilePicture());
        userDTO.setAddress(user.getAddress());
        userDTO.setBio(user.getBio());
        userDTO.setUsername(user.getUsername());
        return userDTO;
    }


    @Override
    public List<UserDTO> findByName(String fullname) {
        List<User> user = userRepository.findByName(fullname);
        return user.stream().filter(item -> item.getFullName() != null).map(item -> {
            UserDTO userDTO = new UserDTO();
            userDTO.setId(item.getId());
            userDTO.setFullName(item.getFullName());
            userDTO.setUrlSocial(item.getUrlSocial());
            userDTO.setEmailContact(item.getEmailContact());
            userDTO.setProfilePicture(item.getProfilePicture());
            userDTO.setAddress(item.getAddress());

            userDTO.setUsername(item.getUsername());
            return userDTO;
        }).toList();
    }


    @Override
    public List<UserDTO> findAll() {
        User currentUser = currentUserService.getUserCurrent();
        Long currentUserId = currentUser.getId();
        return userRepository.findAll().stream().filter(item -> item.getFullName() != null
                && item.getId() != currentUserId).map(user -> {
            UserDTO userDTO = new UserDTO();
            userDTO.setId(user.getId());
            userDTO.setUsername(user.getUsername());
            userDTO.setFullName(user.getFullName());
            Optional<Follow> checkBetWeenUser = followRepository
                    .findByFollowerIdAndFollowingId(currentUserId, user.getId());
            if (checkBetWeenUser.isPresent()) {
                userDTO.setIsFollowing(true);
            }
            return userDTO;
        }).toList();
    }

    @Override
    public void editProfileUser(Long userId,UserDTO userDTO, MultipartFile image) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Not found with username" + userDTO.getUsername()));

        user.setFullName(userDTO.getFullName());
        user.setBio(userDTO.getBio());
        user.setEmailContact(userDTO.getEmailContact());
        user.setAddress(userDTO.getAddress());
        if (image != null) {
            try {
                String url = cloudinaryService.uploadFilePost(image);
                user.setProfilePicture(url);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        user.setUrlSocial(userDTO.getUrlSocial());
        userRepository.save(user);
    }


}
