package com.work.meetup.config;

import com.work.meetup.domain.User;
import com.work.meetup.repository.UserRepository;
import com.work.meetup.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import java.util.Collections;
import java.util.Map;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    @Autowired
    private UserService userService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = new DefaultOAuth2User(Collections.emptyList(), userRequest.getAdditionalParameters(), "sub");

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String id, username, email, profileImage;

        if ("google".equals(registrationId)) {
            id = (String) attributes.get("sub");
            username = (String) attributes.get("name");
            email = (String) attributes.get("email");
            profileImage = (String) attributes.get("picture");
        } else if ("naver".equals(registrationId)) {
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            id = (String) response.get("id");
            username = (String) response.get("nickname");
            email = (String) response.get("email");
            profileImage = (String) response.get("profile_image");
        } else if ("kakao".equals(registrationId)) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

            id = attributes.get("id").toString();
            username = (String) profile.get("nickname");
            email = (String) kakaoAccount.get("email");
            profileImage = (String) profile.get("profile_image_url");
        } else {
            throw new IllegalArgumentException("지원하지 않는 OAuth Provider");
        }

        User user = userService.saveOrUpdate(new User(null, username, email, null, profileImage));

        // JWT 생성
        String token = JwtUtil.generateToken(user.getId().toString(), user.getEmail());

        // JWT를 쿠키에 저장
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        Cookie jwtCookie = new Cookie("JWT-TOKEN", token);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/");
        response.addCookie(jwtCookie);

        return new DefaultOAuth2User(Collections.emptyList(), attributes, "id");
    }
}
