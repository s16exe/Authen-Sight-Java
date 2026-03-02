package org.example.JavaFX_UI;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;


public class UIBuilder {

    private final ImageView imageView1;
    private final ImageView imageView2;
    private final Label similarityLabel;

    private final Runnable loadImage1Handler;
    private final Runnable loadImage2Handler;
    private final Runnable compareHandler;

    public UIBuilder(ImageView imageView1, ImageView imageView2, Label similarityLabel,
                     Runnable loadImage1Handler, Runnable loadImage2Handler,
                     Runnable compareHandler) {
        this.imageView1 = imageView1;
        this.imageView2 = imageView2;
        this.similarityLabel = similarityLabel;
        this.loadImage1Handler = loadImage1Handler;
        this.loadImage2Handler = loadImage2Handler;
        this.compareHandler = compareHandler;
    }

    public Pane build() {
        styleImageView(imageView1);
        styleImageView(imageView2);

        // Buttons for Image 1
        Button uploadRemove1Btn = new Button("Upload Image 1");
        styleButton(uploadRemove1Btn);
        uploadRemove1Btn.setOnAction(e -> {
            if (imageView1.getImage() == null) {
                loadImage1Handler.run();
                uploadRemove1Btn.setText("Remove Image 1");
            } else {
                imageView1.setImage(null);
                uploadRemove1Btn.setText("Upload Image 1");
            }
        });

        VBox imageBox1 = new VBox(10, imageView1, uploadRemove1Btn);
        imageBox1.setAlignment(Pos.CENTER);

        // Buttons for Image 2
        Button uploadRemove2Btn = new Button("Upload Image 2");
        styleButton(uploadRemove2Btn);
        uploadRemove2Btn.setOnAction(e -> {
            if (imageView2.getImage() == null) {
                loadImage2Handler.run();
                uploadRemove2Btn.setText("Remove Image 2");
            } else {
                imageView2.setImage(null);
                uploadRemove2Btn.setText("Upload Image 2");
            }
        });

        VBox imageBox2 = new VBox(10, imageView2, uploadRemove2Btn);
        imageBox2.setAlignment(Pos.CENTER);

        // Compare button
        Button compareBtn = new Button("Compare Images");
        styleButton(compareBtn);
        compareBtn.setOnAction(e -> compareHandler.run());

        HBox imagesBox = new HBox(40, imageBox1, imageBox2);
        imagesBox.setAlignment(Pos.CENTER);
        imagesBox.setPadding(new Insets(10));

        similarityLabel.setTextFill(Color.BLACK);
        similarityLabel.setFont(Font.font(22));

        VBox root = new VBox(20, imagesBox, compareBtn, similarityLabel);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #fff;");

        return root;
    }

    private void styleImageView(ImageView imageView) {
        imageView.setFitWidth(320);
        imageView.setFitHeight(320);
        imageView.setPreserveRatio(true);
        imageView.setStyle("-fx-effect: dropshadow(gaussian, #00000033, 10, 0.5, 0, 0); -fx-background-radius: 10;");
    }

    private void styleButton(Button button) {
        button.setStyle(
                "-fx-background-color: #3B82F6;" +
                        "-fx-background-radius: 25;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 16px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 12 28 12 28;" +
                        "-fx-cursor: hand;"
        );
        button.setOnMouseEntered(e -> button.setStyle(
                "-fx-background-color: #2563EB;" +
                        "-fx-background-radius: 25;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 16px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 12 28 12 28;" +
                        "-fx-cursor: hand;"
        ));
        button.setOnMouseExited(e -> button.setStyle(
                "-fx-background-color: #3B82F6;" +
                        "-fx-background-radius: 25;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 16px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 12 28 12 28;" +
                        "-fx-cursor: hand;"
        ));
    }
}
