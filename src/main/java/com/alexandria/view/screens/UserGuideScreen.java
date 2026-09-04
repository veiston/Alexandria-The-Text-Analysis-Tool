package com.alexandria.view.screens;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class UserGuideScreen extends VBox {

    public UserGuideScreen() {

        setPadding(new Insets(30));
        setSpacing(20);

        Label title = new Label("User Guide Screen");

        getChildren().add(title);
    }
}
