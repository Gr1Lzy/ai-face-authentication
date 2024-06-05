package com.example.aifaceauthentication.controller;

import com.example.aifaceauthentication.exception.PhotoUploadException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class MainController {

    @PostMapping("/upload")
    public String uploadPhoto(@RequestParam("file")MultipartFile file) {
        return null;
    }

}
