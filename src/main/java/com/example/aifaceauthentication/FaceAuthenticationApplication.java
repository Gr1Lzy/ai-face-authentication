package com.example.aifaceauthentication;

import com.example.aifaceauthentication.service.impl.FaceServiceImpl;
import org.opencv.core.Mat;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@SpringBootApplication
public class FaceAuthenticationApplication {

    public static void main(String[] args) {
        SpringApplication.run(FaceAuthenticationApplication.class, args);

        try {
            // Create an instance of ImageServiceImpl
            FaceServiceImpl faceService = new FaceServiceImpl();
            faceService.init(); // Make sure to call init() method to initialize OpenCV and load the model

            // Load the first photo from resources
            MultipartFile photo1 = getFileFromResources("photos/photo1.jpg");

            // Load the second photo from resources
            MultipartFile photo2 = getFileFromResources("photos/img.png");

            // Detect faces in both photos
            Mat face1 = faceService.detectFace(photo1);
            Mat face2 = faceService.detectFace(photo2);

            if (face1 != null && face2 != null) {
                // Extract embeddings for both faces
                byte[] embedding1 = faceService.getEmbedding(face1);
                byte[] embedding2 = faceService.getEmbedding(face2);

                // Compare the embeddings
                boolean isSamePerson = faceService.compareEmbedding(embedding1, embedding2);

                // Print the result
                if (isSamePerson) {
                    System.out.println("The two photos belong to the same person.");
                } else {
                    System.out.println("The two photos do not belong to the same person.");
                }
            } else {
                System.out.println("Face not detected in one or both photos.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static MultipartFile getFileFromResources(String filePath) throws IOException {
        ClassPathResource imgFile = new ClassPathResource(filePath);
        File file = imgFile.getFile();
        FileInputStream input = new FileInputStream(file);
        return new MockMultipartFile("file", file.getName(), "image/jpeg", input);
    }
}
