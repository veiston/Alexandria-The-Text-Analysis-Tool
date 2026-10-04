package com.alexandria.view.components.compare_screen;

import com.alexandria.view.components.shared.ScreenHeader;

import javafx.scene.control.Button;

public class CompareHeader extends ScreenHeader {

    private final SimilarityIndicator similarityIndicator = new SimilarityIndicator();
    private final Actions actions = new Actions("Reader", "Quotations", "Save Findings");

    public CompareHeader() {
        setContent(similarityIndicator, actions);

        // The compare screen has a single view, so the Reader/Quotations switch is
        // hidden entirely.
        actions.getViewToggle().setVisible(false);
        actions.getViewToggle().setManaged(false);
    }

    /** 0-100, or null while there is no result yet. */
    public void setSimilarity(Double percent) {
        similarityIndicator.setSimilarity(percent, null);
    }

    /** Uses the category calculated by TextComparisonService. */
    public void setSimilarity(Double percent, String similarityAmount) {
        similarityIndicator.setSimilarity(percent, similarityAmount);
    }

    public void setOnSave(Runnable handler) {
        actions.setOnSave(handler);
    }

    public Button getSaveButton() {
        return actions.getSaveButton();
    }
}