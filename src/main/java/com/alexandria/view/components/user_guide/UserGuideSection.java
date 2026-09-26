package com.alexandria.view.components.user_guide;

import java.net.URL;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class UserGuideSection extends HBox {

    public UserGuideSection(Node instructions, String imagePath) {
        setSpacing(24);
        setAlignment(Pos.TOP_LEFT);
        HBox.setHgrow(instructions, Priority.ALWAYS);
        getChildren().add(instructions);

        URL imageUrl = getClass().getResource(imagePath);
        if (imageUrl != null) {
            ImageView screenshot = new ImageView(new Image(imageUrl.toExternalForm()));
            screenshot.setPreserveRatio(true);
            screenshot.setSmooth(true);
            screenshot.fitWidthProperty().bind(widthProperty().multiply(0.65));
            getChildren().add(screenshot);
        }
    }
}
