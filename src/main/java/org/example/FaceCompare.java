package org.example;


import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.example.JavaFX_UI.AuthenSightUtils;
import org.example.JavaFX_UI.ImageLoader;
import org.example.JavaFX_UI.UIBuilder;
import org.opencv.core.*;
import ai.onnxruntime.*;





public class FaceCompare extends Application {

    static {
        // Load OpenCV native library
        nu.pattern.OpenCV.loadLocally();
    }

    private ImageView imageView1 = new ImageView();
    private ImageView imageView2 = new ImageView();
    private Label similarityLabel = new Label("Similarity: N/A");

    private ImageLoader imageLoader = new ImageLoader();
    private AuthenSightUtils authenSightUtils = new AuthenSightUtils();
    private FaceEmbeddingExtractor extractor;


    @Override
    public void start(Stage primaryStage) throws Exception{
        String modelPath = "src/main/resources/w600k_r50.onnx";
        String cascadePath = "src/main/resources/haarcascade_frontalface_default.xml";


        extractor = new FaceEmbeddingExtractor(modelPath, cascadePath);

        UIBuilder uiBuilder = new UIBuilder(imageView1, imageView2, similarityLabel,
                () -> loadImage1(primaryStage), () -> loadImage2(primaryStage), this::compareImages);

        Scene scene = new Scene(uiBuilder.build(), 750, 500);
        primaryStage.setTitle("AuthenSight - Java Based Face Recognition");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void loadImage1(Stage stage) {
        Image img = imageLoader.loadImage(stage);
        if (img != null) {
            imageView1.setImage(img);
        }
    }

    private void loadImage2(Stage stage) {
        Image img = imageLoader.loadImage(stage);
        if (img != null) {
            imageView2.setImage(img);
        }
    }

    private void compareImages() {

        try {
            Image img1 = imageView1.getImage();
            Image img2 = imageView2.getImage();
            if (img1 == null || img2 == null) {
                similarityLabel.setText("Please upload both images.");
                return;
            }



            Mat mat1 = authenSightUtils.convertFxImageToMat(img1);
            Mat mat2 = authenSightUtils.convertFxImageToMat(img2);


            float[] emb1 = extractor.getEmbedding(mat1);
            float[] emb2 = extractor.getEmbedding(mat2);
            double similarity = 100.0 * authenSightUtils.cosineSimilarity(emb1, emb2);
            similarityLabel.setText(String.format("Similarity: %.4f", similarity));

//        double similarity = FaceSimilarityCalculator.cosineSimilarity(emb1, emb2);
//        System.out.println("Cosine Similarity: " + similarity);

            if (similarity > 0.6) {
                similarityLabel.setText(String.format("✅ Same person  -> Similarity:%.2f %%", similarity));
            } else {
                similarityLabel.setText(String.format("❌ Different person  -> Similarity:%.2f %%", similarity));
            }
        } catch (Exception ex) {
            similarityLabel.setText("Error: " + ex.getMessage());
            ex.printStackTrace();
        }


    }

    public static void main(String[] args) {
        launch(args);
    }
}
