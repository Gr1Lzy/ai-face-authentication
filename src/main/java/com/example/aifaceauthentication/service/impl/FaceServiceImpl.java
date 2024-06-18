package com.example.aifaceauthentication.service.impl;

import com.example.aifaceauthentication.model.Face;
import com.example.aifaceauthentication.model.User;
import com.example.aifaceauthentication.repository.FaceRepository;
import com.example.aifaceauthentication.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import nu.pattern.OpenCV;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.springframework.core.io.ClassPathResource;
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
import java.util.List;

@Service
@RequiredArgsConstructor
public class FaceServiceImpl {

    private final UserRepository userRepository;
    private final FaceRepository faceRepository;

    private CascadeClassifier faceDetector;
    private byte[] faceNetGraphDef;

    @PostConstruct
    public void init() throws IOException {
        OpenCV.loadLocally();
        faceDetector = new CascadeClassifier(new ClassPathResource("etc/lbpcascade_frontalface_improved.xml").getFile().getAbsolutePath());
        faceNetGraphDef = Files.readAllBytes(new ClassPathResource("etc/20180402-114759.pb").getFile().toPath());
    }

    public Mat detectFace(MultipartFile photo) throws IOException {
        Mat img = convertMultipartFileToMat(photo);
        MatOfRect faceDetections = new MatOfRect();
        faceDetector.detectMultiScale(img, faceDetections);

        for (Rect rect : faceDetections.toArray()) {
            return new Mat(img, rect);
        }
        return null;
    }

    public byte[] getEmbedding(Mat photo) {
        if (photo.channels() == 1) {
            Imgproc.cvtColor(photo, photo, Imgproc.COLOR_GRAY2RGB);
        } else if (photo.channels() == 4) {
            Imgproc.cvtColor(photo, photo, Imgproc.COLOR_BGRA2BGR);
        }

        photo.convertTo(photo, CvType.CV_32F);

        try (Graph graph = new Graph()) {
            graph.importGraphDef(faceNetGraphDef);
            try (Session session = new Session(graph)) {
                float[] imageTensor = convertMatToFloatArray(photo);
                Tensor<Float> inputTensor = Tensor.create(new long[]{1, 160, 160, 3}, FloatBuffer.wrap(imageTensor));

                List<Tensor<?>> outputs = session.runner()
                        .feed("input", inputTensor)
                        .feed("phase_train", Tensor.create(false))
                        .fetch("embeddings")
                        .run();

                float[][] embeddings = new float[1][512];
                outputs.get(0).copyTo(embeddings);

                ByteBuffer byteBuffer = ByteBuffer.allocate(embeddings[0].length * Float.BYTES);
                byteBuffer.asFloatBuffer().put(embeddings[0]);
                return byteBuffer.array();
            }
        }
    }

    @Transactional
    public boolean registerUserAndFace(User user, MultipartFile photo) {
        try {
            Mat detectedFace = detectFace(photo);
            if (detectedFace == null) {
                return false;
            }

            byte[] embedding = getEmbedding(detectedFace);

            Face face = new Face();
            face.setUser(user);
            face.setFaceEmbedding(embedding);

            faceRepository.save(face);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Long loginWithFace(MultipartFile photo) throws IOException {
        Mat detectedFace = detectFace(photo);
        if (detectedFace != null) {
            byte[] currentEmbedding = getEmbedding(detectedFace);
            List<Face> allFaces = faceRepository.findAll();
            for (Face faceEntity : allFaces) {
                if (compareEmbedding(currentEmbedding, faceEntity.getFaceEmbedding())) {
                    return faceEntity.getUser().getId();
                }
            }
        }
        return null;
    }

    public boolean compareEmbedding(byte[] face1, byte[] face2) {
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

        double similarity = dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
        System.out.println("Similarity score: " + similarity);

        return similarity > 0.6;
    }

    private Mat convertMultipartFileToMat(MultipartFile file) throws IOException {
        File convFile = new File(System.getProperty("java.io.tmpdir") + System.currentTimeMillis());
        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(file.getBytes());
        }
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
