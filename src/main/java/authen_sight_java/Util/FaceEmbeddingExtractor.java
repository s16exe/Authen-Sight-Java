package authen_sight_java.Util;

import ai.onnxruntime.*;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.nio.FloatBuffer;
import java.util.Collections;

public class FaceEmbeddingExtractor {

    private CascadeClassifier faceDetector;
    private OrtSession session;
    private OrtEnvironment env;

    public FaceEmbeddingExtractor(String modelPath, String cascadePath) throws OrtException {
        faceDetector = new CascadeClassifier(cascadePath);
        env = OrtEnvironment.getEnvironment();
        OrtSession.SessionOptions opts = new OrtSession.SessionOptions();
        session = env.createSession(modelPath, opts);
    }

    public float[] getEmbedding(Mat img) throws Exception {
        if (img.empty()) {
            throw new Exception("Input image is empty");
        }

        Rect faceRect = detectLargestFace(img);
        if (faceRect == null) {
            throw new Exception("No face detected in input image");
        }

        Mat preprocessed = preprocessFace(img, faceRect);
        return runInference(preprocessed);
    }


    private Rect detectLargestFace(Mat img) {
        Mat gray = new Mat();
        Imgproc.cvtColor(img, gray, Imgproc.COLOR_BGR2GRAY);
        MatOfRect faces = new MatOfRect();
        faceDetector.detectMultiScale(gray, faces, 1.2, 5, 0, new Size(80, 80), new Size());
        Rect[] faceArray = faces.toArray();

        if (faceArray.length == 0) return null;

        Rect largest = faceArray[0];
        for (Rect r : faceArray) {
            if (r.area() > largest.area()) largest = r;
        }
        return largest;
    }

    private Mat preprocessFace(Mat img, Rect faceRect) {
        Mat cropped = new Mat(img, faceRect);
        Mat resized = new Mat();
        Imgproc.resize(cropped, resized, new Size(112, 112));
        Imgproc.cvtColor(resized, resized, Imgproc.COLOR_BGR2RGB);

        resized.convertTo(resized, CvType.CV_32FC3);
        Core.subtract(resized, new Scalar(127.5, 127.5, 127.5), resized);
        Core.divide(resized, new Scalar(127.5, 127.5, 127.5), resized);

        return resized;
    }

    private float[] runInference(Mat preprocessed) throws OrtException {
        java.util.List<Mat> channels = new java.util.ArrayList<>();
        Core.split(preprocessed, channels);

        float[] chw = new float[3 * 112 * 112];
        int offset = 0;
        for (Mat c : channels) {
            float[] data = new float[112 * 112];
            c.get(0, 0, data);
            System.arraycopy(data, 0, chw, offset, data.length);
            offset += data.length;
        }

        OnnxTensor inputTensor = OnnxTensor.createTensor(env, FloatBuffer.wrap(chw), new long[]{1, 3, 112, 112});
        OrtSession.Result output = session.run(Collections.singletonMap("input.1", inputTensor));
        float[][] embedding = (float[][]) output.get(0).getValue();

        return embedding[0];
    }
}
