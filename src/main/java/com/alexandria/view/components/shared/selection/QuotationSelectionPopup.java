package com.alexandria.view.components.shared.selection;

import com.alexandria.model.QuotationType;
import com.alexandria.service.LocalizationService;

import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import java.util.function.Consumer;

public final class QuotationSelectionPopup {
    private final HBox root;
    private final Button directButton;
    private final Button indirectButton;
    private final Button annotationButton;
    private Pane attachedTo;

    public QuotationSelectionPopup(Consumer<QuotationType> onSelect) {
        directButton = createTypeButton(
                () -> onSelect.accept(QuotationType.DIRECT));
        indirectButton = createTypeButton(
                () -> onSelect.accept(QuotationType.INDIRECT));
        annotationButton = createTypeButton(
                () -> onSelect.accept(QuotationType.ANNOTATION));

        root = new HBox(6, directButton, indirectButton, annotationButton);
        root.getStyleClass().add("selection-popup");
        root.setManaged(false);

        refreshText();
        LocalizationService.getInstance().addListener(locale -> refreshText());
    }

    private Button createTypeButton(Runnable action) {
        Button button = new Button();
        button.getStyleClass().add("selection-popup-toggle");
        button.setOnAction(e -> action.run());
        return button;
    }

    private void refreshText() {
        LocalizationService i18n = LocalizationService.getInstance();
        directButton.setText(i18n.get(QuotationType.DIRECT.getLocalizationKey()));
        indirectButton.setText(i18n.get(QuotationType.INDIRECT.getLocalizationKey()));
        annotationButton.setText(i18n.get(QuotationType.ANNOTATION.getLocalizationKey()));
        root.requestLayout();
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
