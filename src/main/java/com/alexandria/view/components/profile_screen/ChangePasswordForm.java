package com.alexandria.view.components.profile_screen;

import java.util.function.Consumer;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import com.alexandria.service.LocalizationService;
import com.alexandria.view.components.shared.form.Form;
import com.alexandria.view.components.shared.form.FormField;
import com.alexandria.view.components.shared.form.validation.PasswordValidator;

public class ChangePasswordForm extends VBox {
    private static final double MAX_WIDTH = 380;
    private final Label title = new Label();
    private final Form form;
    private Consumer<String> onChangePassword = password -> {
    };

    public ChangePasswordForm() {
        getStyleClass().add("modal-card");
        setSpacing(20);
        setPadding(new Insets(24));
        setMaxWidth(MAX_WIDTH);
        setPrefWidth(420);

        title.getStyleClass().add("heading-lg");

        LocalizationService i18n = LocalizationService.getInstance();
        form = new Form.Builder()
                .field("newPassword", i18n.get("profile.newPassword"), FormField.Type.PASSWORD, true, new PasswordValidator())
                .field("confirmPassword", i18n.get("profile.confirmPassword"), FormField.Type.PASSWORD, true)
                .submitLabel(i18n.get("profile.changePassword"))
                .customValidator(values -> {
                    String password = values.get("newPassword");
                    String confirmation = values.get("confirmPassword");

                    if (!password.equals(confirmation)) {
                        return LocalizationService.getInstance().get("vm.passwordsDoNotMatch");
                    }

                    return null;
                })
                .build();

        form.setOnSubmit(values -> onChangePassword.accept(values.get("newPassword")));
        getChildren().addAll(title, form);

        refreshText();
        LocalizationService.getInstance().addListener(locale -> refreshText());
    }

    public void refreshText() {
        LocalizationService i18n = LocalizationService.getInstance();
        title.setText(i18n.get("profile.changePassword"));
        form.setFieldLabel("newPassword", i18n.get("profile.newPassword"));
        form.setFieldLabel("confirmPassword", i18n.get("profile.confirmPassword"));
        form.setSubmitLabel(i18n.get("profile.changePassword"));
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
