package com.example.aifaceauthentication;

import com.example.aifaceauthentication.facial_recognition.FaceRecognitionService;

import org.opencv.core.Mat;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class FaceAuthenticationApplication {

    public static void main(String[] args) {
        try {
            FaceRecognitionService service = new FaceRecognitionService();
            String imagePath1 = "src/main/resources/temporary_img/img.png";
            String imagePath2 = "src/main/resources/temporary_img/img_1.png";

            Mat face1 = service.detectAndCropFace(imagePath1);
            Mat face2 = service.detectAndCropFace(imagePath2);

            if (face1 != null && face2 != null) {
                float[] emb1 = service.extractFaceEmbedding(face1);
                float[] emb2 = service.extractFaceEmbedding(face2);

                double euclideanThreshold = 0.6; // Example threshold for Euclidean distance
                boolean useCosineSimilarity = false; // Set true to use cosine similarity instead

                boolean match = service.areFacesMatching(emb1, emb2, euclideanThreshold, useCosineSimilarity);

                if (match) {
                    System.out.println("Faces match.");
                } else {
                    System.out.println("Faces do not match.");
                }
            } else {
                System.out.println("Face detection failed.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
