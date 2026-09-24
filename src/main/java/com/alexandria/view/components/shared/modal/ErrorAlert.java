package com.alexandria.view.components.shared.modal;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class ErrorAlert {
    public static void show(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);

        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-font-size: 14px;");

        StackPane content = new StackPane(messageLabel);
        content.setMinHeight(80);
        alert.getDialogPane().setContent(content);

        alert.showAndWait();
    }
}
