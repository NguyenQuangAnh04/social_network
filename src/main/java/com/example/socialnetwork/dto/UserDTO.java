package com.example.socialnetwork.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class UserDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    @NotBlank(message = "Vui lòng nhập tài khoản")
    @Size(min = 3, max = 8, message = "Tài khoản phải từ 3 đến 8 kí tự")
    private String username;
    private String password;
    private String fullName;
    @NotBlank(message = "Không được bỏ trống email")
    private String email;
    @NotBlank(message = "Không được bỏ trống confirmpassword")
    private String confirmPassword;
    private String profilePicture;
    private List<PostDTO> postDTOS;
    private String bio;
    private String emailContact;
    private String address;
    private String urlSocial;
    private String otp;
    private List<LikeDTO> likeDTOList;
    private Boolean isFollowing =false;

}
