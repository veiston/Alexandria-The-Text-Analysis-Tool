package com.alexandria.view.components.shared.selection;

import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

public final class QuotationSelectionPopup {
    private final HBox root;
    private Pane attachedTo;

    public QuotationSelectionPopup(Runnable onAdd) {
        Button addButton = new Button("Add quotation");
        addButton.getStyleClass().add("selection-popup-toggle");
        addButton.setOnAction(e -> onAdd.run());

        root = new HBox(addButton);
        root.getStyleClass().add("selection-popup");
        root.setManaged(false);
    }

    public void showAt(Pane overlay, double screenX, double screenY) {
        hide();

        if (overlay == null || overlay.getScene() == null) {
            return;
        }

        Point2D local = overlay.screenToLocal(screenX, screenY);

        if (local == null) {
            return;
        }

        overlay.getChildren().add(root);
        attachedTo = overlay;

        root.applyCss();
        root.autosize();

        double overlayWidth = overlay.getWidth();
        double overlayHeight = overlay.getHeight();

        double x = local.getX();
        double y = local.getY();

        if (overlayWidth > 0) {
            x = Math.max(4, Math.min(x, overlayWidth - root.getWidth() - 4));
        }

        if (overlayHeight > 0) {
            y = Math.max(4, Math.min(y, overlayHeight - root.getHeight() - 4));
        }

        root.relocate(x, y);
    }

    public void hide() {
        if (attachedTo != null) {
            attachedTo.getChildren().remove(root);
            attachedTo = null;
        }
    }

    public boolean isShowing() {
        return attachedTo != null;
    }
}