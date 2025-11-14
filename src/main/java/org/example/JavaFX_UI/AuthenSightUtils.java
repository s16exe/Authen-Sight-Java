package org.example.JavaFX_UI;

import javafx.scene.image.Image;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import javafx.embed.swing.SwingFXUtils;


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

    public Mat convertFxImageToMat(Image fxImage) {
        BufferedImage bImage = SwingFXUtils.fromFXImage(fxImage, null);
        Mat mat;

        if (bImage.getType() == BufferedImage.TYPE_3BYTE_BGR) {
            // Common byte-based BGR image
            byte[] pixels = ((DataBufferByte) bImage.getRaster().getDataBuffer()).getData();
            mat = new Mat(bImage.getHeight(), bImage.getWidth(), CvType.CV_8UC3);
            mat.put(0, 0, pixels);
        } else if (bImage.getType() == BufferedImage.TYPE_INT_RGB || bImage.getType() == BufferedImage.TYPE_INT_ARGB) {
            // Integer-based RGB or ARGB image
            int[] pixels = ((DataBufferInt) bImage.getRaster().getDataBuffer()).getData();
            mat = new Mat(bImage.getHeight(), bImage.getWidth(), CvType.CV_8UC3);
            // Convert from int RGB to byte BGR for Mat
            byte[] matPixels = new byte[bImage.getHeight() * bImage.getWidth() * 3];
            for (int i = 0; i < pixels.length; i++) {
                int argb = pixels[i];
                int r = (argb >> 16) & 0xFF;
                int g = (argb >> 8) & 0xFF;
                int b = argb & 0xFF;

                matPixels[i * 3] = (byte) b;       // OpenCV uses BGR order
                matPixels[i * 3 + 1] = (byte) g;
                matPixels[i * 3 + 2] = (byte) r;
            }
            mat.put(0, 0, matPixels);
        } else {
            throw new IllegalArgumentException("Unsupported BufferedImage type: " + bImage.getType());
        }
        return mat;
    }


}
