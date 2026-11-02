package com.alexandria.view.components.profile_screen;

import java.util.Map;
import java.util.function.Consumer;

import com.alexandria.service.LocalizationService;
import com.alexandria.view.components.shared.form.Form;
import com.alexandria.view.components.shared.form.FormField;
import com.alexandria.view.components.shared.form.validation.EmailValidator;

import javafx.scene.layout.VBox;

public class LoginForm extends VBox {
    private final Form form;
    private Consumer<Map<String, String>> onLogin = values -> {};

    public LoginForm() {
        getStyleClass().add("login-form");
        LocalizationService i18n = LocalizationService.getInstance();

        form = new Form.Builder()
                .field("email", i18n.get("profile.email"), FormField.Type.TEXT, true, new EmailValidator())
                .field("password", i18n.get("profile.password"), FormField.Type.PASSWORD, true)
                .submitLabel(i18n.get("profile.logIn"))
                .build();

        // Logins if user presses enter (Readymade listener in Javafx) 
        form.getSubmitButton().setDefaultButton(true);
        form.setOnSubmit(values -> onLogin.accept(values));
        getChildren().add(form);

        refreshText();
        LocalizationService.getInstance().addListener(locale -> refreshText());
    }

    public void refreshText() {
        LocalizationService i18n = LocalizationService.getInstance();
        form.setFieldLabel("email", i18n.get("profile.email"));
        form.setFieldLabel("password", i18n.get("profile.password"));
        form.setSubmitLabel(i18n.get("profile.logIn"));
    }

    public void setOnLogin(Consumer<Map<String, String>> handler) {
        this.onLogin = handler;
    }

    public void showError(String message) {
        form.showError(message);
    }

    public void reset() {
        form.reset();
    }
}
