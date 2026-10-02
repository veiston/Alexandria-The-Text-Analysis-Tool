package com.alexandria.view.components.compare_screen;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class ComparisonSidePanel extends VBox {

    private final Label title = new Label("Comparison panel");
    private final Label subtitle = new Label("Coming later");

    public ComparisonSidePanel() {
        getStyleClass().add("comparison-side-panel");

        title.getStyleClass().add("text-muted");
        subtitle.getStyleClass().add("text-muted");

        setSpacing(6);
        setAlignment(Pos.CENTER);

        getChildren().addAll(title, subtitle);

        setMinSize(0, 0);
        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    }

    public void clear() {
        // Placeholder for future comparison controls.
    }
}
