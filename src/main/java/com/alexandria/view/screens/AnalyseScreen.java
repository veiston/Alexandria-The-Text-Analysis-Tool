package com.alexandria.view.screens;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class AnalyseScreen extends VBox {

    public AnalyseScreen() {

        setPadding(new Insets(30));
        setSpacing(20);

        Label title = new Label("Analyse Screen");

        getChildren().add(title);
    }
}
