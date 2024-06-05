package com.example.aifaceauthentication.facial_recognition;

import org.nd4j.common.io.ClassPathResource;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Service
public class FaceRecognitionService {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    private CascadeClassifier faceDetector;

    public FaceRecognitionService() throws IOException {
        faceDetector = new CascadeClassifier(loadCascadeClassifier("/etc/lbpcascade_frontalface_improved.xml"));
    }

    private String loadCascadeClassifier(String path) throws IOException {
        InputStream inputStream = new ClassPathResource(path).getInputStream();
        Path tempFile = Files.createTempFile("cascade", ".xml");
        Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
        return tempFile.toAbsolutePath().toString();
    }

    public Mat detectAndCropFace(String imagePath) {
        Mat image = Imgcodecs.imread(imagePath);
        Mat grayImage = new Mat();
        Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);
        MatOfRect faceDetections = new MatOfRect();
        faceDetector.detectMultiScale(grayImage, faceDetections);

        for (Rect rect : faceDetections.toArray()) {
            return new Mat(image, rect);
        }
        return null;
    }

    public boolean saveImage(Mat image, String outputPath) {
        return Imgcodecs.imwrite(outputPath, image);
    }
}