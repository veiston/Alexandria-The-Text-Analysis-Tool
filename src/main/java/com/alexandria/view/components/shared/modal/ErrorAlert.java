package com.alexandria.view.components.shared.modal;

import com.alexandria.service.LocalizationService;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class ErrorAlert {
    public static void show(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        LocalizationService i18n = LocalizationService.getInstance();
        alert.setTitle(i18n.get("error"));
        alert.setHeaderText(null);

        ButtonType okButton = new ButtonType(i18n.get("ok"), ButtonBar.ButtonData.OK_DONE);
        alert.getButtonTypes().setAll(okButton);

        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-font-size: 14px;");

        StackPane content = new StackPane(messageLabel);
        content.setMinHeight(80);
        alert.getDialogPane().setContent(content);

        alert.showAndWait();
    }
}
