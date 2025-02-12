package com.work.meetup.dto;


import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor

public class AuthResponseDto {

    private String token;
    private String message;


    public AuthResponseDto(String token, String message) {
        this.token = token;
        this.message = message;
    }
}
