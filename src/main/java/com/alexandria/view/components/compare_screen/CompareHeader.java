package com.alexandria.view.components.compare_screen;

import com.alexandria.view.components.shared.ScreenHeader;

import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;

import java.util.function.IntConsumer;

public class CompareHeader extends ScreenHeader {

    private final SimilarityIndicator similarityIndicator = new SimilarityIndicator();
    private final Actions actions = new Actions("Reader", "Quotations", "Save Findings");

    public CompareHeader() {
        setContent(similarityIndicator, actions);
    }

    /** 0-100, or null while there is no result yet. */
    public void setSimilarity(Double percent) {
        similarityIndicator.setSimilarity(percent);
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