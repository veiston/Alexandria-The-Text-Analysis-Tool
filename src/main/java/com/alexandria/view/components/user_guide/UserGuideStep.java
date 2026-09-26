package com.alexandria.view.components.user_guide;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class UserGuideStep extends HBox {

    public UserGuideStep(int number, String text) {
        Label numberLabel = new Label(String.valueOf(number));
        numberLabel.getStyleClass().add("user-guide-number");

        Label textLabel = new Label(text);
        textLabel.getStyleClass().add("user-guide-row-text");
        textLabel.setWrapText(true);
        textLabel.setMinWidth(0);
        textLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(textLabel, Priority.ALWAYS);

        setSpacing(10);
        setAlignment(Pos.TOP_LEFT);
        setMinWidth(0);
        getChildren().addAll(numberLabel, textLabel);
        getStyleClass().add("user-guide-step");
    }
}
