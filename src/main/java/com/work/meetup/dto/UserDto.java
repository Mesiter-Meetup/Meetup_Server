package com.work.meetup.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import com.work.meetup.domain.User;

@Getter
@Setter
@NoArgsConstructor
public class UserDto {
    private String username;
    private String email;
    private String profile;

    public UserDto(User user) {
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.profile = user.getProfile();
    }


}
