package com.alexandria.view.components.profile_screen;

import java.util.Map;
import java.util.function.Consumer;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import com.alexandria.service.LocalizationService;
import com.alexandria.view.components.shared.toggle.Toggle;

public class AuthView extends VBox {
    private final LoginForm loginForm;
    private final RegisterForm registerForm;
    private final Toggle toggle;
    private final VBox formHost;
    private final Label title = new Label();

    private Consumer<Map<String, String>> onLogin = values -> {
    };
    private Consumer<Map<String, String>> onRegister = values -> {
    };

    public AuthView() {
        LocalizationService i18n = LocalizationService.getInstance();
        getStyleClass().add("auth-view");
        getStyleClass().add("modal-card");
        setPrefWidth(420);
        setSpacing(20);
        setPadding(new Insets(24));

        title.getStyleClass().add("heading-lg");

        toggle = new Toggle(i18n.get("profile.logIn"), i18n.get("profile.createAccount"));

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

        refreshText();
        LocalizationService.getInstance().addListener(locale -> refreshText());
    }

    public void refreshText() {
        LocalizationService i18n = LocalizationService.getInstance();
        title.setText(i18n.get("profile.welcome"));
        toggle.setLabels(i18n.get("profile.logIn"), i18n.get("profile.createAccount"));
        if (loginForm != null) {
            loginForm.refreshText();
        }
        if (registerForm != null) {
            registerForm.refreshText();
        }
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
