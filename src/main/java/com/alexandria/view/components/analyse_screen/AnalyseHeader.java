package com.alexandria.view.components.analyse_screen;

import com.alexandria.view.components.shared.toggle.Toggle;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.function.IntConsumer;

public class AnalyseHeader extends HBox {

    public enum ViewMode { READER, QUOTATIONS }

    private final Label titleLabel = new Label();
    private final Label subtitleLabel = new Label();
    private final Toggle viewToggle = new Toggle("Reader", "Quotations");
    private final Button saveButton = new Button("Save Findings");

    private IntConsumer onViewChange = i -> {};
    private Runnable onSave = () -> {};

    public AnalyseHeader() {
        getStyleClass().add("analyse-header");
        setAlignment(Pos.CENTER_LEFT);
        setSpacing(16);

        titleLabel.getStyleClass().add("heading-lg");
        subtitleLabel.getStyleClass().add("text-muted");
        VBox titleBox = new VBox(2, titleLabel, subtitleLabel);

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        saveButton.getStyleClass().addAll("button", "primary");
        saveButton.setOnAction(e -> onSave.run());

        viewToggle.setOnToggle(i -> onViewChange.accept(i));

        HBox actions = new HBox(12, viewToggle, saveButton);
        actions.setAlignment(Pos.CENTER_RIGHT);

        getChildren().addAll(titleBox, spacer, actions);
    }

    public void setTitle(String title, String subtitle) {
        titleLabel.setText(title);
        subtitleLabel.setText(subtitle != null ? subtitle : "");
    }

    public void setOnViewChange(IntConsumer handler) { this.onViewChange = handler; }
    public void setOnSave(Runnable handler) { this.onSave = handler; }
    public void resetToReader() { viewToggle.setSelectedIndex(0); }  
}
