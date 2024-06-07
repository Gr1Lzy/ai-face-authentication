package com.example.aifaceauthentication.service;

import com.example.aifaceauthentication.dto.user.UserLoginRequestDto;
import com.example.aifaceauthentication.dto.user.UserLoginResponseDto;
import com.example.aifaceauthentication.dto.user.UserRegisterRequestDto;
import com.example.aifaceauthentication.dto.user.UserResponseDto;

public interface AuthenticationService {
    UserResponseDto register(UserRegisterRequestDto requestDto);
    UserLoginResponseDto login(UserLoginRequestDto requestDto);
    UserResponseDto getUserByEmail(String email);
}
