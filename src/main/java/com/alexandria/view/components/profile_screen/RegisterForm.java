package com.alexandria.view.components.profile_screen;

import com.alexandria.utils.EmailValidator;
import com.alexandria.utils.PasswordValidator;
import com.alexandria.view.components.shared.form.Form;
import com.alexandria.view.components.shared.form.FormField;

import javafx.scene.layout.VBox;
import java.util.Map;
import java.util.function.Consumer;

public class RegisterForm extends VBox {
    private final Form form;
    private Consumer<Map<String, String>> onRegister = values -> {
    };

    public RegisterForm() {

        getStyleClass().add("register-form");

        form = new Form.Builder()
                .field("name", "Username", FormField.Type.TEXT, true)
                .field("email", "Email", FormField.Type.EMAIL, true)
                .field("password", "Password", FormField.Type.PASSWORD, true)
                .field("organization", "Organization", FormField.Type.TEXT, false)
                .field("photo", "Profile Photo", FormField.Type.FILE, false)

                .submitLabel("Create Account")
                .customValidator(values -> {

                    String email = values.get("email");
                    if (!EmailValidator.isValid(email)) {
                        return "Please enter a valid email address.";
                    }

                    String password = values.get("password");
                    if (!PasswordValidator.isValid(password)) {
                        return "Password must be at least 8 characters.";
                    }

                    return null;
                })
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
