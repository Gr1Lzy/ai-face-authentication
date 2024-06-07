package com.example.aifaceauthentication.controller;

import com.example.aifaceauthentication.dto.user.UserLoginRequestDto;
import com.example.aifaceauthentication.dto.user.UserLoginResponseDto;
import com.example.aifaceauthentication.dto.user.UserRegisterRequestDto;
import com.example.aifaceauthentication.dto.user.UserResponseDto;
import com.example.aifaceauthentication.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(@Validated @RequestBody UserRegisterRequestDto requestDto) {
        logger.info("Received register request: {}", requestDto);

        UserResponseDto userResponseDto = authenticationService.register(requestDto);
        return ResponseEntity.ok(userResponseDto);
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponseDto> authenticate(@Validated @RequestBody UserLoginRequestDto requestDto) {
        UserLoginResponseDto userLoginResponseDto = authenticationService.login(requestDto);
        return ResponseEntity.ok(userLoginResponseDto);
    }

    @GetMapping("/info")
    public ResponseEntity<UserResponseDto> getUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        UserResponseDto userResponseDto = authenticationService.getUserByEmail(email);
        return ResponseEntity.ok(userResponseDto);
    }
}

