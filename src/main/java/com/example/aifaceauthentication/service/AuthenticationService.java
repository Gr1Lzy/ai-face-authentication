package com.example.aifaceauthentication.service;

import com.example.aifaceauthentication.dto.user.UserLoginRequestDto;
import com.example.aifaceauthentication.dto.user.UserLoginResponseDto;
import com.example.aifaceauthentication.dto.user.UserRegisterRequestDto;
import com.example.aifaceauthentication.dto.user.UserResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface AuthenticationService {
    UserResponseDto register(UserRegisterRequestDto requestDto, MultipartFile photo) throws IOException;
    UserLoginResponseDto login(UserLoginRequestDto requestDto);
    UserResponseDto getUserByEmail(String email);
    UserLoginResponseDto loginByPhoto(MultipartFile photo);
}
