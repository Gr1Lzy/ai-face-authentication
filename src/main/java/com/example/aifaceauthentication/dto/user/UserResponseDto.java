package com.example.aifaceauthentication.dto.user;

import lombok.Data;

@Data
public class UserResponseDto {
    private Long id;
    private String login;
    private String email;
    private String name;
    private String surname;
}
