package com.work.meetup.service;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Service;

import com.work.meetup.config.JwtUtil;
import com.work.meetup.dto.TokenResponse;


@Service
public class JwtService {

    private final JwtUtil jwtUtil;

    public JwtService(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    public String generateAccessToken(String email) {
        return jwtUtil.generateAccessToken(email);
    }

    // ✅ 리프레시 토큰 생성 (여기 추가!)
    public String generateRefreshToken(String email) {
        return jwtUtil.generateRefreshToken(email);
    }

    // ✅ 리프레시 토큰을 검증하고 새 액세스 토큰 발급
    public TokenResponse refreshToken(String refreshToken) {
        String email = jwtUtil.validateToken(refreshToken);

        if (email != null) {
            String newAccessToken = jwtUtil.generateAccessToken(email);
            String newRefreshToken = jwtUtil.generateRefreshToken(email);
            return new TokenResponse(newAccessToken, newRefreshToken);
        }
        throw new RuntimeException("Invalid refresh token");
    }
}
