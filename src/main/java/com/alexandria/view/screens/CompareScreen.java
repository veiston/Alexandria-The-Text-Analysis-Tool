package com.alexandria.view.screens;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class CompareScreen extends VBox {

    public CompareScreen() {

        setPadding(new Insets(30));
        setSpacing(20);

        Label title = new Label("Compare Screen");

        getChildren().add(title);
    }
}