package com.work.meetup.service;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import java.util.Map;
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
        String provider = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> userInfo = extractUserInfo(provider, attributes);

        String email = (String) userInfo.get("email");
        String name = (String) userInfo.getOrDefault("name", "Unknown");

        // 기존 유저 확인 또는 새 유저 생성
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> createUser(email, name, provider));

        // JWT 토큰 생성
        String accessToken = jwtService.generateAccessToken(user.getEmail());
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());

        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        // ✅ OAuth2User를 반환하기 위해 DefaultOAuth2User 사용
        return new DefaultOAuth2User(
                Collections.emptyList(), // 권한 (필요하면 수정 가능)
                attributes,               // OAuth2 응답 정보
                "email"                    // 고유 식별자 키 (Google: "sub", Naver: "response", Kakao: "id")
        );
    }

    private User createUser(String email, String name, String provider) {
        User user = User.builder()
                .email(email)
                .username(name)
                .provider(User.AuthProvider.valueOf(provider.toUpperCase()))
                .build();
        return userRepository.save(user);
    }

    private Map<String, Object> extractUserInfo(String provider, Map<String, Object> attributes) {
        if ("naver".equals(provider)) {
            return (Map<String, Object>) attributes.get("response");
        } else if ("kakao".equals(provider)) {
            return (Map<String, Object>) attributes.get("kakao_account");
        }
        return attributes;
    }
}

