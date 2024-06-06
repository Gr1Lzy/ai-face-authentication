package com.example.aifaceauthentication.controller;

import com.example.aifaceauthentication.dto.user.UserLoginRequestDto;
import com.example.aifaceauthentication.dto.user.UserLoginResponseDto;
import com.example.aifaceauthentication.dto.user.UserRegisterRequestDto;
import com.example.aifaceauthentication.dto.user.UserResponseDto;
import com.example.aifaceauthentication.exception.RegistrationException;
import com.example.aifaceauthentication.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(@Validated @RequestBody UserRegisterRequestDto requestDto) {
        logger.info("Received registration request for email: {}", requestDto.getEmail());
        try {
            UserResponseDto userResponseDto = authenticationService.register(requestDto);
            logger.info("User registered successfully: {}", requestDto.getEmail());
            return ResponseEntity.ok(userResponseDto);
        } catch (RegistrationException e) {
            logger.error("Registration failed for email: {}", requestDto.getEmail(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            logger.error("Unexpected error during registration for email: {}", requestDto.getEmail(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponseDto> authenticate(@Validated @RequestBody UserLoginRequestDto requestDto) {
        logger.info("Received login request for email: {}", requestDto.getEmail());
        try {
            UserLoginResponseDto userLoginResponseDto = authenticationService.login(requestDto);
            logger.info("User logged in successfully: {}", requestDto.getEmail());
            return ResponseEntity.ok(userLoginResponseDto);
        } catch (Exception e) {
            logger.error("Login failed for email: {}", requestDto.getEmail(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }
}
