package com.alexandria.view.components.shared.document.highlight;

import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.text.TextFlow;

public final class TxtTextLayout {

    private TxtTextLayout() {
    }

    public static Path shapeFor(TextFlow flow, int indexA, int indexB, String styleClass) {
        int start = Math.max(0, Math.min(indexA, indexB));
        int end = Math.max(indexA, indexB);

        PathElement[] elements = flow.rangeShape(start, end);

        Path path = new Path(elements);
        path.setManaged(false);
        path.setMouseTransparent(true);

        if (styleClass != null && !styleClass.isBlank()) {
            path.getStyleClass().add(styleClass);
        }

        return path;
    }
}
