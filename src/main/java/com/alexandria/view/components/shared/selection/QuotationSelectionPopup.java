package com.alexandria.view.components.shared.selection;

import com.alexandria.model.QuotationType;

import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import java.util.function.Consumer;

public final class QuotationSelectionPopup {
    private final HBox root;
    private Pane attachedTo;

    public QuotationSelectionPopup(Consumer<QuotationType> onSelect) {
        Button directBtn = createTypeButton("Direct citation",
                () -> onSelect.accept(QuotationType.DIRECT));
        Button indirectBtn = createTypeButton("Indirect citation",
                () -> onSelect.accept(QuotationType.INDIRECT));
        Button annotationBtn = createTypeButton("Annotation",
                () -> onSelect.accept(QuotationType.ANNOTATION));

        root = new HBox(6, directBtn, indirectBtn, annotationBtn);
        root.getStyleClass().add("selection-popup");
        root.setManaged(false);
    }

    private Button createTypeButton(String text, Runnable action) {
        Button button = new Button(text);
        button.getStyleClass().add("selection-popup-toggle");
        button.setOnAction(e -> action.run());
        return button;
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
