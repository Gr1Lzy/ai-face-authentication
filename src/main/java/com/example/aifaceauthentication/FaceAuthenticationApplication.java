package com.example.aifaceauthentication;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RequiredArgsConstructor
public class FaceAuthenticationApplication {

    public static void main(String[] args) {
        SpringApplication.run(FaceAuthenticationApplication.class, args);
    }
}
