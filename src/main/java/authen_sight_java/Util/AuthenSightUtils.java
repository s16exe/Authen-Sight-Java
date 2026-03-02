package authen_sight_java.Util;

import javafx.scene.image.Image;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import javafx.embed.swing.SwingFXUtils;


import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;

public class AuthenSightUtils {


    public double cosineSimilarity(float[] a, float[] b) {
        double dot = 0.0, normA = 0.0, normB = 0.0;
        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    public static BufferedImage convertTo3ByteBGR(BufferedImage image) {
        if (image.getType() == BufferedImage.TYPE_3BYTE_BGR) {
            return image;
        }
        BufferedImage convertedImg = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D g2d = convertedImg.createGraphics();
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();
        return convertedImg;
    }

    public Mat convertFxImageToMat(Image fxImage) {
        BufferedImage bImage = SwingFXUtils.fromFXImage(fxImage, null);
        bImage = convertTo3ByteBGR(bImage);
        byte[] pixels = ((DataBufferByte) bImage.getRaster().getDataBuffer()).getData();
        Mat mat = new Mat(bImage.getHeight(), bImage.getWidth(), CvType.CV_8UC3);
        mat.put(0, 0, pixels);
        return mat;
    }



}
