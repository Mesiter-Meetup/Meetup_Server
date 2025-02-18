package com.work.meetup.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.work.meetup.domain.User;
import com.work.meetup.dto.SignupRequest;
import com.work.meetup.dto.LoginRequest;
import com.work.meetup.dto.TokenResponse;
import com.work.meetup.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public String registerUser(SignupRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return "이미 가입된 이메일입니다.";
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .username(request.getUsername())
                .provider(User.AuthProvider.LOCAL)
                .build();

        userRepository.save(user);
        return "회원가입 성공";
    }

    @Transactional
    public TokenResponse login(LoginRequest request) {
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                String accessToken = jwtService.generateAccessToken(user.getEmail());
                String refreshToken = jwtService.generateRefreshToken(user.getEmail());

                user.setRefreshToken(refreshToken);
                userRepository.save(user);

                return new TokenResponse(accessToken, refreshToken);
            }
        }
        return null;
    }
}
