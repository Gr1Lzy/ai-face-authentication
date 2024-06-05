package com.example.aifaceauthentication.dto.user;

import lombok.Data;

@Data
public class UserLoginRequestDto {
    private String login;
    private String password;
}
