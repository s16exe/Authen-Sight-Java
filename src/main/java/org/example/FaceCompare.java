package org.example;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import ai.onnxruntime.*;

import java.nio.FloatBuffer;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;

public class FaceCompare {
    static {
        // Load OpenCV native library
        nu.pattern.OpenCV.loadLocally();
    }

    public static void main(String[] args) throws Exception {
        String modelPath = "src/main/resources/w600k_r50.onnx";
        String cascadePath = "src/main/resources/haarcascade_frontalface_default.xml";

        String img1Path = "src/main/resources/images/rohit.jpg";
        String img2Path = "src/main/resources/images/virat2.jpg";

        FaceCompare app = new FaceCompare();
        app.run(modelPath, cascadePath, img1Path, img2Path);
    }

    public void run(String modelPath, String cascadePath, String img1, String img2) throws Exception {
        CascadeClassifier faceDetector = new CascadeClassifier(cascadePath);
        OrtEnvironment env = OrtEnvironment.getEnvironment();
        OrtSession.SessionOptions opts = new OrtSession.SessionOptions();
        OrtSession session = env.createSession(modelPath, opts);

        // Compute embeddings
        float[] emb1 = getEmbedding(session, env, faceDetector, img1);
        float[] emb2 = getEmbedding(session, env, faceDetector, img2);

        double similarity = cosineSimilarity(emb1, emb2);
        System.out.println("Cosine Similarity: " + similarity);

        if (similarity > 0.6) {
            System.out.println("✅ Same person");
        } else {
            System.out.println("❌ Different person");
        }
    }

    private float[] getEmbedding(OrtSession session, OrtEnvironment env, CascadeClassifier faceDetector, String imagePath) throws Exception {
        Mat img = Imgcodecs.imread(imagePath);
        if (img.empty()) {
            throw new Exception("Cannot read image: " + imagePath);
        }

        // Detect face
        Mat gray = new Mat();
        Imgproc.cvtColor(img, gray, Imgproc.COLOR_BGR2GRAY);
        MatOfRect faces = new MatOfRect();
        faceDetector.detectMultiScale(gray, faces, 1.2, 5, 0, new Size(80, 80), new Size());
        Rect[] faceArray = faces.toArray();
        if (faceArray.length == 0) {
            throw new Exception("No face detected in " + imagePath);
        }

        // Choose largest face
        Rect bestFace = faceArray[0];
        for (Rect r : faceArray) {
            if (r.area() > bestFace.area()) bestFace = r;
        }

        Mat cropped = new Mat(img, bestFace);
        Mat resized = new Mat();
        Imgproc.resize(cropped, resized, new Size(112, 112));
        Imgproc.cvtColor(resized, resized, Imgproc.COLOR_BGR2RGB);

        // Convert to float and normalize [-1,1]
        resized.convertTo(resized, CvType.CV_32FC3);
        Core.subtract(resized, new Scalar(127.5, 127.5, 127.5), resized);
        Core.divide(resized, new Scalar(127.5, 127.5, 127.5), resized);

        // Convert HWC → CHW
        java.util.List<Mat> channelList = new java.util.ArrayList<>();
        Core.split(resized, channelList);

        float[] chw = new float[3 * 112 * 112];
        int offset = 0;
        for (Mat c : channelList) {
            float[] data = new float[112 * 112];
            c.get(0, 0, data);
            System.arraycopy(data, 0, chw, offset, data.length);
            offset += data.length;
        }

        // Create ONNX input tensor
        OnnxTensor inputTensor = OnnxTensor.createTensor(env, FloatBuffer.wrap(chw), new long[]{1, 3, 112, 112});
        OrtSession.Result output = session.run(Collections.singletonMap("input.1", inputTensor));
        float[][] embedding = (float[][]) output.get(0).getValue();

        return embedding[0];
    }


    private double cosineSimilarity(float[] a, float[] b) {
        double dot = 0.0, normA = 0.0, normB = 0.0;
        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}

