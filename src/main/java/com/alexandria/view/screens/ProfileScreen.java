package com.alexandria.view.screens;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class ProfileScreen extends VBox {

    public ProfileScreen() {

        setPadding(new Insets(30));
        setSpacing(20);

        Label title = new Label("Profile Screen");

        getChildren().add(title);
    }
}