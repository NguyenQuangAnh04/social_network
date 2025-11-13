package com.example.socialnetwork.service;

import com.example.socialnetwork.enums.Role;
import com.example.socialnetwork.model.User;
import com.example.socialnetwork.repository.RoleRepository;
import com.example.socialnetwork.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomOidcUserService implements OAuth2UserService<OidcUserRequest, OidcUser> {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = new OidcUserService().loadUser(userRequest);

        String email = oidcUser.getAttribute("email");
        String name = oidcUser.getAttribute("name");
        String picture = oidcUser.getAttribute("picture");
        String username = email.split("@")[0];
        com.example.socialnetwork.model.Role role = roleRepository.findByRole(Role.USER);

        User user = userRepository.findByEmail(email).orElseGet(() ->
                User.builder()
                        .email(email)
                        .fullName(name)
                        .username(username)
                        .profilePicture(picture)
                        .provider("google")
                        .password(UUID.randomUUID().toString())
                        .roles(Collections.singleton(role))

                        .build()
        );
        userRepository.save(user);
        return oidcUser;
    }
}
