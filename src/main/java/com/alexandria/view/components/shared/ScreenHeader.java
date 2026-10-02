package com.alexandria.view.components.shared;

import com.alexandria.view.components.shared.toggle.Toggle;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.util.function.IntConsumer;

public class ScreenHeader extends HBox {

    public ScreenHeader() {
        getStyleClass().add("screen-header");
        setAlignment(Pos.CENTER_LEFT);
        setSpacing(16);
    }

    protected void setContent(Node left, Node right) {
        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        getChildren().setAll(left, spacer, right);
    }

    /**
     * Right side of a header: view toggle (Reader / Quotations) plus the primary
     * save button.
     */
    public static class Actions extends HBox {

        private final Toggle viewToggle;
        private final Button saveButton;

        private IntConsumer onViewChange = index -> {
        };
        private Runnable onSave = () -> {
        };

        public Actions(String firstView, String secondView, String saveLabel) {
            super(12);
            setAlignment(Pos.CENTER_RIGHT);

            viewToggle = new Toggle(firstView, secondView);
            viewToggle.setOnToggle(index -> onViewChange.accept(index));

            saveButton = new Button(saveLabel);
            saveButton.getStyleClass().addAll("button", "primary");
            saveButton.setOnAction(e -> onSave.run());

            getChildren().addAll(viewToggle, saveButton);
        }

        public void setOnViewChange(IntConsumer handler) {
            onViewChange = handler == null ? index -> {
            } : handler;
        }

        public void setOnSave(Runnable handler) {
            onSave = handler == null ? () -> {
            } : handler;
        }

        public void resetToFirstView() {
            viewToggle.setSelectedIndex(0);
        }

        public Toggle getViewToggle() {
            return viewToggle;
        }

        public Button getSaveButton() {
            return saveButton;
        }
    }
}