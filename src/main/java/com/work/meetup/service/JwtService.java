package com.work.meetup.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import com.work.meetup.dto.TokenResponse;

@Service
public class JwtService {

    private final Key key;
    private final long expirationTime;
    private final long refreshExpirationTime;


    public JwtService(@Value("${jwt.secret}") String secretKey,
                      @Value("${jwt.expiration}") long expirationTime,
                      @Value("${jwt.refreshExpiration}") long refreshExpirationTime) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
        this.expirationTime = expirationTime;
        this.refreshExpirationTime = refreshExpirationTime;


    }

    public String generateAccessToken(String email) {
        return generateToken(email, expirationTime);
    }

    public String generateRefreshToken(String email) {
        return generateToken(email, refreshExpirationTime);
    }

    private String generateToken(String email, long expiry) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiry))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String validateToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (JwtException e) {
            return null;
        }
    }

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
