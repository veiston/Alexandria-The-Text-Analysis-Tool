package com.alexandria.view.components.shared.modal;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class ConfirmationAlert {

    public static boolean show(String title, String header, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);

        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }
}
