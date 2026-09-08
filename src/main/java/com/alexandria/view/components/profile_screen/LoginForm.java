package com.alexandria.view.components.profile_screen;

import java.util.Map;
import java.util.function.Consumer;

import javafx.scene.layout.VBox;

import com.alexandria.view.components.shared.form.Form;
import com.alexandria.view.components.shared.form.FormField;
import com.alexandria.view.components.shared.form.validation.EmailValidator;

public class LoginForm extends VBox {
    private final Form form;
    private Consumer<Map<String, String>> onLogin = values -> {};

    public LoginForm() {
        getStyleClass().add("login-form");

        form = new Form.Builder()
                .field("email", "Email", FormField.Type.TEXT, true, new EmailValidator())
                .field("password", "Password", FormField.Type.PASSWORD, true)
                .submitLabel("Log In")
                .build();

        form.setOnSubmit(values -> onLogin.accept(values));
        getChildren().add(form);
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

