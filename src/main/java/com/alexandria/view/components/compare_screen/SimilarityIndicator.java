package com.alexandria.view.components.compare_screen;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class SimilarityIndicator extends HBox {

    private static final String HIGH = "similarity-badge-high";
    private static final String MODERATE = "similarity-badge-moderate";
    private static final String LOW = "similarity-badge-low";

    private final Label percentLabel = new Label("--");
    private final Label levelBadge = new Label();
    private final DoubleProperty fraction = new SimpleDoubleProperty(0);

    public SimilarityIndicator() {
        getStyleClass().add("similarity-indicator");
        setAlignment(Pos.CENTER_LEFT);
        setSpacing(12);

        Label caption = new Label("Similarity Index:");
        caption.getStyleClass().add("heading-md");

        percentLabel.getStyleClass().add("similarity-percent");
        levelBadge.getStyleClass().add("similarity-badge");

        HBox valueRow = new HBox(8, percentLabel, levelBadge);
        valueRow.setAlignment(Pos.CENTER_LEFT);

        Region fill = new Region();
        fill.getStyleClass().add("meter-fill");

        StackPane track = new StackPane(fill);
        track.getStyleClass().add("meter-track");
        track.setPrefSize(0, 6);
        track.setMinHeight(6);
        track.setMaxHeight(6);
        StackPane.setAlignment(fill, Pos.CENTER_LEFT);
        fill.maxWidthProperty().bind(track.widthProperty().multiply(fraction));

        getChildren().addAll(caption, new VBox(4, valueRow, track));

        clear();
    }

    public void setSimilarity(Double percent, String similarityAmount) {
        if (percent == null || percent.isNaN()) {
            clear();
            return;
        }

        double value = Math.max(0, Math.min(100, percent));

        percentLabel.setText(Math.round(value) + "%");
        fraction.set(value / 100);

        levelBadge.getStyleClass().removeAll(HIGH, MODERATE, LOW);

        if ("High".equalsIgnoreCase(similarityAmount)) {
            levelBadge.setText("HIGH CORRELATION");
            levelBadge.getStyleClass().add(HIGH);
        } else if ("Moderate".equalsIgnoreCase(similarityAmount)) {
            levelBadge.setText("MODERATE CORRELATION");
            levelBadge.getStyleClass().add(MODERATE);
        } else if ("Low".equalsIgnoreCase(similarityAmount)) {
            levelBadge.setText("LOW CORRELATION");
            levelBadge.getStyleClass().add(LOW);
        } else {
            levelBadge.setVisible(false);
            levelBadge.setManaged(false);
            return;
        }

        levelBadge.setVisible(true);
        levelBadge.setManaged(true);
    }

    public void clear() {
        percentLabel.setText("--");
        fraction.set(0);
        levelBadge.setVisible(false);
        levelBadge.setManaged(false);
    }
}