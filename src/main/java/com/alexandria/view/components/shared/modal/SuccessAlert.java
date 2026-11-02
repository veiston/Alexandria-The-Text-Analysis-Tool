package com.alexandria.view.components.shared.modal;

import com.alexandria.service.LocalizationService;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

public class SuccessAlert {

    public static void show(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        LocalizationService i18n = LocalizationService.getInstance();
        alert.setTitle(i18n.get("success"));
        alert.setHeaderText(null);
        alert.setContentText(message);

        ButtonType okButton = new ButtonType(i18n.get("ok"), ButtonBar.ButtonData.OK_DONE);
        alert.getButtonTypes().setAll(okButton);

        alert.showAndWait();
    }
}
