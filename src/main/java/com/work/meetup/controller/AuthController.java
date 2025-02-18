package com.work.meetup.controller;

import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import com.work.meetup.dto.SignupRequest;
import com.work.meetup.dto.LoginRequest;
import com.work.meetup.dto.TokenResponse;
import com.work.meetup.service.AuthService;
import com.work.meetup.service.JwtService;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    @PostMapping("/signup")
    public String registerUser(@RequestBody SignupRequest request) {
        return authService.registerUser(request);
    }

    @PostMapping("/login")
    public TokenResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);  // 클라이언트(LocalStorage)에 저장
    }

    @PostMapping("/refresh")
    public TokenResponse refresh(@RequestParam String refreshToken) {
        return jwtService.refreshToken(refreshToken);
    }
}
