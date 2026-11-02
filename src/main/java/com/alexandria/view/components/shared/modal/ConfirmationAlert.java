package com.alexandria.view.components.shared.modal;

import com.alexandria.service.LocalizationService;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

public class ConfirmationAlert {

    public static boolean show(String title, String header, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);

        LocalizationService i18n = LocalizationService.getInstance();
        ButtonType okButton = new ButtonType(i18n.get("ok"), ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType(i18n.get("cancel"), ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(okButton, cancelButton);

        return alert.showAndWait().orElse(cancelButton) == okButton;
    }
}
