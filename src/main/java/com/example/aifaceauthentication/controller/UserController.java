package com.example.aifaceauthentication.controller;

import com.example.aifaceauthentication.dto.user.UserLoginRequestDto;
import com.example.aifaceauthentication.dto.user.UserLoginResponseDto;
import com.example.aifaceauthentication.dto.user.UserRegisterRequestDto;
import com.example.aifaceauthentication.dto.user.UserResponseDto;
import com.example.aifaceauthentication.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class UserController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(@Validated @RequestBody UserRegisterRequestDto requestDto,
                                                    @RequestParam("photo") MultipartFile photo) throws IOException {
        UserResponseDto userResponseDto = authenticationService.register(requestDto, photo);
        return ResponseEntity.ok(userResponseDto);
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponseDto> authenticate(@Validated @RequestBody UserLoginRequestDto requestDto) {
        UserLoginResponseDto userLoginResponseDto = authenticationService.login(requestDto);
        return ResponseEntity.ok(userLoginResponseDto);
    }

    @PostMapping("/login/photo")
    public ResponseEntity<UserLoginResponseDto> authenticateByPhoto(@RequestParam("photo") MultipartFile photo) {
        UserLoginResponseDto userLoginResponseDto = authenticationService.loginByPhoto(photo);
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


