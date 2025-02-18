package com.work.meetup.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import com.work.meetup.dto.UserDto;
import com.work.meetup.repository.UserRepository;

import java.util.Optional;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    // ✅ 현재 로그인한 사용자 정보 조회 (OAuth2 + JWT)
    @GetMapping("/profile")
    public UserDto getUserProfile(@AuthenticationPrincipal OAuth2User user) {
        String email = user.getAttribute("email");
        return userRepository.findByEmail(email)
                .map(u -> new UserDto(u.getEmail(), u.getUsername()))
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
