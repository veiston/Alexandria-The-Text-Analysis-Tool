package com.alexandria.view.components.shared;

import javafx.scene.Node;
import javafx.scene.layout.VBox;

/** Shared card shell for contextual passages in analysis and comparison. */
public class ContextMatchCard extends VBox {
    public ContextMatchCard(Node... content) {
        setSpacing(4);
        getStyleClass().add("context-match-card");
        getChildren().addAll(content);
    }
}