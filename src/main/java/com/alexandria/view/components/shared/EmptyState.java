package com.alexandria.view.components.shared;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class EmptyState extends VBox {

    public EmptyState(String titleText, String subtitleText) {
        getStyleClass().add("empty-state");
        setSpacing(8);
        setAlignment(Pos.CENTER);

        Label title = new Label(titleText);
        title.getStyleClass().add("heading-lg");
        getChildren().add(title);

        if (subtitleText != null && !subtitleText.isBlank()) {
            Label subtitle = new Label(subtitleText);
            subtitle.getStyleClass().add("text-muted");
            subtitle.getStyleClass().add("empty-state-subtitle");
            getChildren().add(subtitle);
        }
    }
}
