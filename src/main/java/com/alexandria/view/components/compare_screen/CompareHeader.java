package com.alexandria.view.components.compare_screen;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class CompareHeader extends HBox {

    private final Label title = new Label("Compare");
    private final Label subtitle = new Label();

    private final Button readerButton = new Button("Reader");
    private final Button quotationsButton = new Button("Quotations");
    private final Button saveButton = new Button("Save findings");

    public CompareHeader() {
        getStyleClass().add("compare-header");

        title.getStyleClass().add("heading-lg");
        subtitle.getStyleClass().add("text-muted");

        readerButton.getStyleClass().add("header-view-button");
        quotationsButton.getStyleClass().add("header-view-button");
        saveButton.getStyleClass().add("header-primary-button");

        VBox titleBox = new VBox(2, title, subtitle);

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox actions = new HBox(
                8,
                readerButton,
                quotationsButton,
                saveButton);

        actions.setAlignment(Pos.CENTER_RIGHT);

        setAlignment(Pos.CENTER_LEFT);
        setSpacing(12);

        getChildren().addAll(titleBox, spacer, actions);
    }

    public void setTitle(
            String documentATitle,
            String documentBTitle) {

        title.setText("Compare");

        subtitle.setText(
                safe(documentATitle)
                        + " vs "
                        + safe(documentBTitle));
    }

    private String safe(String value) {
        return value == null || value.isBlank()
                ? "Document"
                : value;
    }

    public void resetToReader() {
        readerButton.getStyleClass().add("active");
        quotationsButton.getStyleClass().remove("active");
    }

    public Button getReaderButton() {
        return readerButton;
    }

    public Button getQuotationsButton() {
        return quotationsButton;
    }

    public Button getSaveButton() {
        return saveButton;
    }

    public void setOnSave(Runnable handler) {
        saveButton.setOnAction(
                event -> {
                    if (handler != null) {
                        handler.run();
                    }
                });
    }

    public void setOnReader(Runnable handler) {
        readerButton.setOnAction(
                event -> {
                    if (handler != null) {
                        handler.run();
                    }
                });
    }

    public void setOnQuotations(Runnable handler) {
        quotationsButton.setOnAction(
                event -> {
                    if (handler != null) {
                        handler.run();
                    }
                });
    }
}
