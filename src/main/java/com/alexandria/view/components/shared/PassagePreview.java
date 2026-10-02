package com.alexandria.view.components.shared;

import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/** Shared location, action and passage-preview block for analysis sidebars. */
public class PassagePreview extends VBox {
    public PassagePreview(String location, String snippet, Runnable onGoTo) {
        setSpacing(3);

        Label locationLabel = new Label(location == null ? "" : location);
        locationLabel.getStyleClass().add("context-match-page");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Hyperlink goTo = new Hyperlink("Go to →");
        goTo.getStyleClass().add("hyperlink");
        goTo.setOnAction(event -> {
            if (onGoTo != null) {
                onGoTo.run();
            }
        });

        HBox header = new HBox(locationLabel, spacer, goTo);
        header.setAlignment(Pos.CENTER_LEFT);

        Label passage = new Label(snippet == null ? "" : snippet);
        passage.setWrapText(true);
        passage.getStyleClass().add("context-match-snippet");

        getChildren().addAll(header, passage);
    }
}