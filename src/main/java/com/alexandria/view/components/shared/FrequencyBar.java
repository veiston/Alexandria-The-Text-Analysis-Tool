package com.alexandria.view.components.shared;

import javafx.geometry.Pos;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

/** Shared visual meter for word-frequency and tracked-word rows. */
public class FrequencyBar extends StackPane {
    private final Region bar = new Region();

    public FrequencyBar() {
        Region track = new Region();
        track.getStyleClass().add("track");

        bar.getStyleClass().add("bar");
        getStyleClass().add("frequency-bar");
        setMinWidth(0);
        setPrefHeight(7);
        setMaxHeight(7);

        StackPane.setAlignment(track, Pos.CENTER_LEFT);
        StackPane.setAlignment(bar, Pos.CENTER_LEFT);
        bar.setMaxWidth(Region.USE_PREF_SIZE);
        getChildren().addAll(track, bar);
    }

    public void setRatio(double ratio) {
        double safeRatio = Math.max(0, Math.min(1, ratio));
        bar.prefWidthProperty().unbind();
        bar.prefWidthProperty().bind(widthProperty().multiply(safeRatio));
    }
}