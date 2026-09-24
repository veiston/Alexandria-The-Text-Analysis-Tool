package com.alexandria.view.components.shared.modal;

import javafx.scene.control.Alert;

public class SuccessAlert {

    public static void show(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
