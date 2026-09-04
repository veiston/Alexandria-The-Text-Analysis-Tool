package com.alexandria.view.screens;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class LibraryScreen extends VBox {

    public LibraryScreen() {

        setPadding(new Insets(30));
        setSpacing(20);

        Label title = new Label("Library Screen");

        getChildren().add(title);
    }
}