package com.alexandria.view.components.profile_screen;

import java.util.Map;
import java.util.function.Consumer;

import javafx.scene.layout.VBox;

import com.alexandria.service.LocalizationService;
import com.alexandria.view.components.shared.form.Form;
import com.alexandria.view.components.shared.form.FormField;
import com.alexandria.view.components.shared.form.validation.EmailValidator;
import com.alexandria.view.components.shared.form.validation.FileValidator;
import com.alexandria.view.components.shared.form.validation.PasswordValidator;
import com.alexandria.view.components.shared.form.validation.TextValidator;

public class RegisterForm extends VBox {
    private final Form form;
    private Consumer<Map<String, String>> onRegister = values -> {};

    public RegisterForm() {
        getStyleClass().add("register-form");
        LocalizationService i18n = LocalizationService.getInstance();

        form = new Form.Builder()
                .field("name", i18n.get("profile.username"), FormField.Type.TEXT, true, new TextValidator())
                .field("email", i18n.get("profile.email"), FormField.Type.TEXT, true, new EmailValidator())
                .field("password", i18n.get("profile.password"), FormField.Type.PASSWORD, true, new PasswordValidator())
                .field("organization", i18n.get("profile.organization"), FormField.Type.TEXT, false, new TextValidator())
                .field("photo", i18n.get("profile.photo"), FormField.Type.IMG_FILE, false, new FileValidator())
                .submitLabel(i18n.get("profile.createAccount"))
                .build();

        form.getSubmitButton().setDefaultButton(true);
        form.setOnSubmit(values -> onRegister.accept(values));
        getChildren().add(form);

        refreshText();
        LocalizationService.getInstance().addListener(locale -> refreshText());
    }

    public void refreshText() {
        LocalizationService i18n = LocalizationService.getInstance();
        form.setFieldLabel("name", i18n.get("profile.username"));
        form.setFieldLabel("email", i18n.get("profile.email"));
        form.setFieldLabel("password", i18n.get("profile.password"));
        form.setFieldLabel("organization", i18n.get("profile.organization"));
        form.setFieldLabel("photo", i18n.get("profile.photo"));
        form.setSubmitLabel(i18n.get("profile.createAccount"));
        form.refreshFileLabels();
    }

    public void setOnRegister(Consumer<Map<String, String>> handler) {
        this.onRegister = handler;
    }

    public void showError(String message) {
        form.showError(message);
    }

    public void reset() {
        form.reset();
    }
}
