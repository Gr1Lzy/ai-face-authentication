package com.example.aifaceauthentication.controller;

import com.example.aifaceauthentication.dto.user.UserLoginRequestDto;
import com.example.aifaceauthentication.dto.user.UserLoginResponseDto;
import com.example.aifaceauthentication.dto.user.UserRegisterRequestDto;
import com.example.aifaceauthentication.dto.user.UserResponseDto;
import com.example.aifaceauthentication.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class UserController {
    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public UserResponseDto register(@Validated @RequestBody UserRegisterRequestDto requestDto) {
        return authenticationService.register(requestDto);
    }

    @PostMapping("/login")
    public UserLoginResponseDto authenticate(@Validated @RequestBody UserLoginRequestDto requestDto) {
        return authenticationService.login(requestDto);
    }
}
