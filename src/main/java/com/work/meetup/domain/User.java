package com.work.meetup.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import java.util.Collections;
import java.util.Collection;
import lombok.NoArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;




@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String username;

    @Enumerated(EnumType.STRING)
    private AuthProvider provider;

//    @Column(length = 500)
//    private String refreshToken;

    public enum AuthProvider {
        GOOGLE, NAVER, KAKAO, LOCAL
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList(); // 권한이 필요하면 수정 가능
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email; // Spring Security는 username을 email로 사용 가능
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 계정 만료 여부 (true: 만료되지 않음)
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 계정 잠금 여부 (true: 잠기지 않음)
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 비밀번호 만료 여부 (true: 만료되지 않음)
    }

    @Override
    public boolean isEnabled() {
        return true; // 계정 활성화 여부 (true: 활성화됨)
    }
}

