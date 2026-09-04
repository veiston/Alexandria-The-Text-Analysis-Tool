package com.alexandria.view.screens;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class ArchiveScreen extends VBox {

    public ArchiveScreen() {

        setPadding(new Insets(30));
        setSpacing(20);

        Label title = new Label("Archive Screen");

        getChildren().add(title);
    }
}
