package com.alexandria.view.screens;

import java.util.Map;
import java.util.function.Consumer;

import com.alexandria.controller.UserSessionController;
import com.alexandria.model.User;
import com.alexandria.view.components.profile_screen.AuthView;
import com.alexandria.view.components.profile_screen.ChangePasswordForm;
import com.alexandria.view.components.profile_screen.EditProfileForm;
import com.alexandria.view.components.profile_screen.ProfileView;
import com.alexandria.view.components.shared.modal.Modal;

import javafx.geometry.Pos;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class ProfileScreen extends StackPane {
    private final UserSessionController session = UserSessionController.getInstance();
    private final VBox content = new VBox();
    private final ProfileView profileView = new ProfileView();
    private final AuthView authView = new AuthView();
    private final EditProfileForm editProfileForm = new EditProfileForm();
    private final ChangePasswordForm changePasswordForm = new ChangePasswordForm();
    private final Modal modal = new Modal();

    private Consumer<Map<String, String>> onLogin = values -> {
    };
    private Consumer<Map<String, String>> onRegister = values -> {
    };
    private Consumer<Map<String, String>> onEditProfile = values -> {
    };
    private Consumer<String> onChangePassword = password -> {
    };

    private Runnable onLogout = () -> {
    };
    private Runnable onDeleteAccount = () -> {
    };

    public ProfileScreen() {
        getStyleClass().add("profile-screen");
        content.getStyleClass().add("profile-content");

        configureProfileView();
        configureAuthView();
        configureForms();

        getChildren().addAll(content, modal);

        session.addListener(user -> refresh());

        refresh();
    }

    /* UI composition */

    private void configureProfileView() {
        profileView.setOnEdit(this::showEditProfileModal);
        profileView.setOnChangePassword(this::showChangePasswordModal);
        profileView.setOnLogout(() -> onLogout.run());
        profileView.setOnDeleteAccount(() -> onDeleteAccount.run());
    }

    private void configureAuthView() {
        authView.setOnLogin(values -> onLogin.accept(values));
        authView.setOnRegister(values -> onRegister.accept(values));
    }

    private void configureForms() {
        editProfileForm.setOnSave(values -> onEditProfile.accept(values));
        changePasswordForm.setOnChangePassword(password -> onChangePassword.accept(password));
    }

    /* Screen state */

    private void refresh() {
        content.getChildren().clear();
        User user = session.getCurrentUser();

        if (user != null) {
            showLoggedIn(user);
        } else {
            showAuthModal();
        }
    }

    private void showLoggedIn(User user) {
        modal.hide();
        content.setAlignment(Pos.TOP_LEFT);
        profileView.setUser(user);
        VBox.setVgrow(profileView, Priority.ALWAYS);
        content.getChildren().add(profileView);
    }

    /* Modal composition */
    private void showAuthModal() {
        authView.reset();
        modal.show(authView, false);
    }

    private void showEditProfileModal() {
        editProfileForm.reset();
        editProfileForm.prefill(session.getCurrentUser());
        modal.show(editProfileForm);
    }

    private void showChangePasswordModal() {
        changePasswordForm.reset();
        modal.show(changePasswordForm);
    }

    public void closeModal() {
        modal.hide();
    }

    /* Error handling */

    public void showLoginError(String message) {
        authView.showLoginError(message);
    }

    public void showRegisterError(String message) {
        authView.showRegisterError(message);
    }

    public void showEditProfileError(String message) {
        editProfileForm.showError(message);
    }

    public void showChangePasswordError(String message) {
        changePasswordForm.showError(message);
    }

    /* Controller events */

    public void setOnLogin(Consumer<Map<String, String>> handler) {
        this.onLogin = handler;
    }

    public void setOnRegister(Consumer<Map<String, String>> handler) {
        this.onRegister = handler;
    }

    public void setOnEditProfile(Consumer<Map<String, String>> handler) {
        this.onEditProfile = handler;
    }

    public void setOnChangePassword(Consumer<String> handler) {
        this.onChangePassword = handler;
    }

    public void setOnLogout(Runnable handler) {
        this.onLogout = handler;
    }

    public void setOnDeleteAccount(Runnable handler) {
        this.onDeleteAccount = handler;
    }
}