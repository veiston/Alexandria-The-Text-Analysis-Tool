package com.alexandria.view.components.profile_screen;

import java.util.Map;
import java.util.function.Consumer;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import com.alexandria.view.components.shared.toggle.Toggle;

public class AuthView extends VBox {
    private final LoginForm loginForm;
    private final RegisterForm registerForm;
    private final Toggle toggle;
    private final VBox formHost;
    private Consumer<Map<String, String>> onLogin = values -> {
    };
    private Consumer<Map<String, String>> onRegister = values -> {
    };

    public AuthView() {
        getStyleClass().add("auth-view");
        getStyleClass().add("modal-card");
        setSpacing(20);
        setPadding(new Insets(24));

        Label title = new Label("Welcome to Alexandria");
        title.getStyleClass().add("heading-lg");

        toggle = new Toggle("Log In", "Create Account");

        formHost = new VBox();
        loginForm = new LoginForm();
        registerForm = new RegisterForm();
        loginForm.setOnLogin(values -> onLogin.accept(values));
        registerForm.setOnRegister(values -> onRegister.accept(values));
        formHost.getChildren().add(loginForm);

        toggle.setOnToggle(index -> {
            if (index == 0) {
                showLogin();
            } else {
                showRegister();
            }
        });

        getChildren().addAll(title, toggle, formHost);
    }

    private void showLogin() {
        loginForm.reset();
        formHost.getChildren().setAll(loginForm);
    }

    private void showRegister() {
        registerForm.reset();
        formHost.getChildren().setAll(registerForm);
    }

    public void reset() {
        toggle.setSelectedIndex(0);
        showLogin();
    }

    public void setOnLogin(Consumer<Map<String, String>> handler) {
        this.onLogin = handler;
    }

    public void setOnRegister(Consumer<Map<String, String>> handler) {
        this.onRegister = handler;
    }

    public void showLoginError(String message) {
        loginForm.showError(message);
    }

    public void showRegisterError(String message) {
        registerForm.showError(message);
    }

    public LoginForm getLoginForm() {
        return loginForm;
    }

    public RegisterForm getRegisterForm() {
        return registerForm;
    }
}
