package com.alexandria.view.screens;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class SettingsScreen extends VBox {

    public SettingsScreen() {

        setPadding(new Insets(30));
        setSpacing(20);

        Label title = new Label("Settings Screen");

        getChildren().add(title);
    }
}