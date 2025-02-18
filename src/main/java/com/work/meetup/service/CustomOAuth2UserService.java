package com.work.meetup.service;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.Optional;
import com.work.meetup.repository.UserRepository;
import com.work.meetup.domain.User;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import java.util.Collections;


@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    public CustomOAuth2UserService(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = (String) attributes.get("email");

        Optional<User> existingUser = userRepository.findByEmail(email);
        User user = existingUser.orElseGet(() -> createUser(email));

        // JWT 발급
        String accessToken = jwtService.generateAccessToken(user.getEmail());
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());

        return new DefaultOAuth2User(Collections.emptyList(), attributes, "email");
    }

    private User createUser(String email) {
        User user = User.builder()
                .email(email)
                .provider(User.AuthProvider.GOOGLE)
                .build();
        return userRepository.save(user);
    }
}
