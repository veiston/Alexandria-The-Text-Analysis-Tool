package com.alexandria.view.components.profile_screen;

import java.util.function.Consumer;

import com.alexandria.utils.PasswordValidator;
import com.alexandria.view.components.shared.form.Form;
import com.alexandria.view.components.shared.form.FormField;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class ChangePasswordForm extends VBox {
    private static final double MAX_WIDTH = 380;
    private final Form form;
    private Consumer<String> onChangePassword = newPassword -> {
    };

    public ChangePasswordForm() {
        getStyleClass().add("modal-card");
        setSpacing(20);
        setPadding(new Insets(24));
        setMaxWidth(MAX_WIDTH);

        Label title = new Label("Change Password");
        title.getStyleClass().add("heading-lg");

        form = new Form.Builder()
                .field("newPassword", "New Password", FormField.Type.PASSWORD, true)
                .field("confirmPassword", "Confirm Password", FormField.Type.PASSWORD, true)
                .submitLabel("Change Password")
                .customValidator(values -> {
                    String pw = values.get("newPassword");
                    String confirm = values.get("confirmPassword");

                    if (!PasswordValidator.isValid(pw)) {
                        return "Password must be at least 8 characters.";
                    }
                    if (!pw.equals(confirm)) {
                        return "Passwords do not match.";
                    }

                    return null;
                })
                .build();

        form.setOnSubmit(values -> onChangePassword.accept(values.get("newPassword")));
        getChildren().addAll(title, form);
    }

    public void setOnChangePassword(Consumer<String> handler) {
        this.onChangePassword = handler;
    }

    public void showError(String message) {
        form.showError(message);
    }

    public void reset() {
        form.reset();
    }
}
