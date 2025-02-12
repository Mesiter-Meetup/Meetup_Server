package com.work.meetup.controller;


import com.work.meetup.domain.User;
import com.work.meetup.service.UserService;
import com.work.meetup.config.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // 회원가입 API
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody Map<String, String> user) {
        String email = user.get("email");
        String username = user.get("username");
        String password = passwordEncoder.encode(user.get("password"));

        Optional<User> existingUser = userService.findByEmail(email);
        if (existingUser.isPresent()) {
            return ResponseEntity.badRequest().body("이미 가입된 이메일입니다.");
        }

        userService.save(new User(null, username, email, password, null));
        return ResponseEntity.ok("회원가입 성공");
    }

    // 로그인 API
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> user, HttpServletResponse response) {
        String email = user.get("email");
        String password = user.get("password");

        Optional<User> existingUser = userService.findByEmail(email);
        if (existingUser.isEmpty() || !passwordEncoder.matches(password, existingUser.get().getPassword())) {
            return ResponseEntity.status(401).body("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        User loggedInUser = existingUser.get();
        String token = JwtUtil.generateToken(loggedInUser.getId(), loggedInUser.getEmail());

        Cookie jwtCookie = new Cookie("JWT-TOKEN", token);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/");
        response.addCookie(jwtCookie);

        return ResponseEntity.ok("로그인 성공");
    }
}
