package com.alexandria.view.components.user_guide;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class UserGuideSection extends HBox {

    public UserGuideSection(Node instructions, String imagePath) {
        ImageView screenshot = new ImageView(new Image(getClass().getResource(imagePath).toExternalForm()));
        screenshot.setPreserveRatio(true);
        screenshot.setSmooth(true);

        setSpacing(24);
        setAlignment(Pos.TOP_LEFT);
        HBox.setHgrow(instructions, Priority.ALWAYS);
        screenshot.fitWidthProperty().bind(widthProperty().multiply(0.65));
        getChildren().addAll(instructions, screenshot);
    }
}
