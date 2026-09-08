package com.alexandria.view.components.profile_screen;

import java.util.Map;
import java.util.function.Consumer;

import javafx.scene.layout.VBox;

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

        form = new Form.Builder()
                .field("name", "Username", FormField.Type.TEXT, true, new TextValidator())
                .field("email", "Email", FormField.Type.TEXT, true, new EmailValidator())
                .field("password", "Password", FormField.Type.PASSWORD, true, new PasswordValidator())
                .field("organization", "Organization", FormField.Type.TEXT, false, new TextValidator())
                .field("photo", "Profile Photo", FormField.Type.IMG_FILE, false, new FileValidator())
                .submitLabel("Create Account")
                .build();

        form.setOnSubmit(values -> onRegister.accept(values));
        getChildren().add(form);
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
