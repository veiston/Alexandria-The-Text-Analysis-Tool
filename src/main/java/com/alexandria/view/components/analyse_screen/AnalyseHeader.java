package com.alexandria.view.components.analyse_screen;

import com.alexandria.view.components.shared.ScreenHeader;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;

import java.util.function.IntConsumer;

public class AnalyseHeader extends ScreenHeader {

    public enum ViewMode {
        READER, QUOTATIONS
    }

    private final Label titleLabel = new Label();
    private final Label subtitleLabel = new Label();
    private final Actions actions = new Actions("Reader", "Quotations", "Save Findings");

    public AnalyseHeader() {
        titleLabel.getStyleClass().add("heading-lg");
        subtitleLabel.getStyleClass().add("text-muted");

        setContent(new VBox(2, titleLabel, subtitleLabel), actions);
    }

    public void setTitle(String title, String subtitle) {
        titleLabel.setText(title);
        subtitleLabel.setText(subtitle != null ? subtitle : "");
    }

    public void setOnViewChange(IntConsumer handler) {
        actions.setOnViewChange(handler);
    }

    public void setOnSave(Runnable handler) {
        actions.setOnSave(handler);
    }

    public void resetToReader() {
        actions.resetToFirstView();
    }

    public ToggleButton getQuotationsButton() {
        return actions.getViewToggle().getButton(1);
    }

    public Button getSaveButton() {
        return actions.getSaveButton();
    }
}