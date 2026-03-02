package authen_sight_java;


import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import authen_sight_java.Util.AuthenSightUtils;
import authen_sight_java.Util.ImageLoader;
import authen_sight_java.JavaFX_UI.UIBuilder;
import authen_sight_java.Util.FaceEmbeddingExtractor;
import org.opencv.core.*;


public class FaceCompare extends Application {

    static {
        // Load OpenCV native library
        nu.pattern.OpenCV.loadLocally();
    }

    private final ImageView imageView1;
    private final ImageView imageView2;
    private final Label similarityLabel = new Label("Similarity: N/A");

    private final ImageLoader imageLoader;
    private final AuthenSightUtils authenSightUtils = new AuthenSightUtils();
    private FaceEmbeddingExtractor extractor;

    public FaceCompare() {
        imageLoader = new ImageLoader();
        imageView2 = new ImageView();
        imageView1 = new ImageView();
    }


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


            if (similarity > 60) {
                similarityLabel.setText(String.format("✅ Same person  -> Similarity:%.2f %%", similarity));
            } else {
                similarityLabel.setText(String.format("❌ Different person  -> Similarity:%.2f %%", similarity));
            }
        } catch (Exception ex) {
            similarityLabel.setText("Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
