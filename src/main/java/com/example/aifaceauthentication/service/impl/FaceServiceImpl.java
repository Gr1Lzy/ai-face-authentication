package com.example.aifaceauthentication.service.impl;

import com.example.aifaceauthentication.model.Face;
import com.example.aifaceauthentication.repository.FaceRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.tensorflow.Graph;
import org.tensorflow.Session;
import org.tensorflow.Tensor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FaceServiceImpl {
    private final FaceRepository faceRepository;

    private CascadeClassifier faceDetector;
    private byte[] faceNetGraphDef;

    @PostConstruct
    public void init() throws IOException {
        nu.pattern.OpenCV.loadLocally();
        faceDetector = new CascadeClassifier("src/main/resources/etc/lbpcascade_frontalface_improved.xml");

        Path modelPath = Paths.get("src/main/resources/etc/20180402-114759.pb");
        faceNetGraphDef = Files.readAllBytes(modelPath);
    }

    public Mat detectFace(MultipartFile photo) throws IOException {
        Mat img = convertMultipartFileToMat(photo);
        MatOfRect faceDetections = new MatOfRect();
        faceDetector.detectMultiScale(img, faceDetections);

        for (Rect rect : faceDetections.toArray()) {
            Mat face = new Mat(img, rect);
            return face;
        }
        return null;
    }

    public byte[] getEmbedding(Mat photo) {
        try (Graph graph = new Graph()) {
            graph.importGraphDef(faceNetGraphDef);
            try (Session session = new Session(graph)) {
                float[] imageTensor = convertMatToFloatArray(photo);
                Tensor<Float> inputTensor = Tensor.create(new long[]{1, 160, 160, 3}, FloatBuffer.wrap(imageTensor));

                List<Tensor<?>> outputs = session.runner()
                        .feed("input", inputTensor)
                        .fetch("embeddings")
                        .run();

                float[] embedding = new float[512];
                outputs.get(0).copyTo(embedding);

                // Convert float[] to byte[]
                byte[] embeddingBytes = new byte[embedding.length * Float.BYTES];
                ByteBuffer byteBuffer = ByteBuffer.allocate(embedding.length * Float.BYTES);
                byteBuffer.asFloatBuffer().put(embedding);
                byteBuffer.get(embeddingBytes);

                return embeddingBytes;
            }
        }
    }

    public boolean registerFace(MultipartFile photo, Long userId) throws IOException {
        Mat face = detectFace(photo);
        if (face != null) {
            byte[] embedding = getEmbedding(face);
            Face faceEntity = new Face();
            faceEntity.setUserId(userId);
            faceEntity.setFaceEmbedding(embedding);
            faceRepository.save(faceEntity);
            return true;
        }
        return false;
    }

    public Long loginWithFace(MultipartFile photo) throws IOException {
        Mat face = detectFace(photo);
        if (face != null) {
            byte[] currentEmbedding = getEmbedding(face);
            List<Face> allFaces = faceRepository.findAll();
            for (Face faceEntity : allFaces) {
                if (compareEmbedding(currentEmbedding, faceEntity.getFaceEmbedding())) {
                    return faceEntity.getUserId();
                }
            }
        }
        return null;
    }

    private boolean compareEmbedding(byte[] face1, byte[] face2) {
        float[] embedding1 = new float[face1.length / Float.BYTES];
        float[] embedding2 = new float[face2.length / Float.BYTES];

        ByteBuffer.wrap(face1).asFloatBuffer().get(embedding1);
        ByteBuffer.wrap(face2).asFloatBuffer().get(embedding2);

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (int i = 0; i < embedding1.length; i++) {
            dotProduct += embedding1[i] * embedding2[i];
            normA += Math.pow(embedding1[i], 2);
            normB += Math.pow(embedding2[i], 2);
        }
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB)) > 0.9;
    }

    private Mat convertMultipartFileToMat(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();

        return Imgcodecs.imread(convFile.getAbsolutePath());
    }

    private float[] convertMatToFloatArray(Mat mat) {
        Mat resizedImage = new Mat();
        Imgproc.resize(mat, resizedImage, new Size(160, 160));

        int width = resizedImage.cols();
        int height = resizedImage.rows();
        int channels = resizedImage.channels();
        float[] floatArray = new float[width * height * channels];
        resizedImage.get(0, 0, floatArray);

        for (int i = 0; i < floatArray.length; i++) {
            floatArray[i] = floatArray[i] / 255.0f;
        }

        return floatArray;
    }
}
