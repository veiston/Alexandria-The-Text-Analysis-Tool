package com.alexandria.controller;

import java.sql.SQLException;
import java.util.Map;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import com.alexandria.dao.UserDAO;
import com.alexandria.model.User;
import com.alexandria.utils.PasswordHasher;
import com.alexandria.view.screens.ProfileScreen;

/**
 * Mediator between profile UI events and user related business logic.
 */
public class ProfileController {
    private final UserDAO userDAO;
    private final UserSessionController session = UserSessionController.getInstance();

    /*
     * Constructor used for testing purposes, allow DAO to be testes without screen
     */
    ProfileController(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public ProfileController(UserDAO userDAO, ProfileScreen profileScreen) {
        this.userDAO = userDAO;

        /* Authentication */
        profileScreen.setOnLogin(values -> {
            Result result = login(values.get("email"), values.get("password"));

            if (!result.success()) {
                profileScreen.showLoginError(result.message());
            }
        });

        profileScreen.setOnRegister(values -> {
            Result result = register(values);

            if (!result.success()) {
                profileScreen.showRegisterError(
                        result.message());
            }
        });

        /* Profile */
        profileScreen.setOnEditProfile(values -> {
            Result result = editProfile(values);

            if (result.success()) {
                profileScreen.closeModal();
            } else {
                profileScreen.showEditProfileError(
                        result.message());
            }
        });

        /* Security */
        profileScreen.setOnChangePassword(password -> {
            Result result = changePassword(password);

            if (result.success()) {
                profileScreen.closeModal();
            } else {
                profileScreen.showChangePasswordError(
                        result.message());
            }
        });

        /* Danger zone */
        profileScreen.setOnLogout(session::logout);
        profileScreen.setOnDeleteAccount(this::deleteAccount);
    }

    /* Authentication */

    public Result register(Map<String, String> values) {

        String name = values.get("name");
        String email = values.get("email");
        String password = values.get("password");
        String photo = values.get("photo");
        String organization = values.get("organization");

        try {
            if (userDAO.findByEmail(email) != null) {
                return Result.error("An account with this email already exists.");
            }

            User user = new User(name, email, photo, organization, PasswordHasher.hash(password));
            userDAO.create(user);
            session.login(user);

            return Result.ok("Account created successfully.");

        } catch (SQLException e) {
            return Result.error("Registration failed: " + e.getMessage());
        }
    }

    public Result login(String email, String password) {

        try {
            User user = userDAO.findByEmail(email);

            if (user == null || !PasswordHasher.matches(password, user.getPassword())) {
                return Result.error("Incorrect email or password.");
            }

            session.login(user);

            return Result.ok("Logged in successfully.");

        } catch (SQLException e) {
            return Result.error("Login failed: " + e.getMessage());
        }
    }

    /* Profile */

    public Result editProfile(
            Map<String, String> values) {

        User user = session.getCurrentUser();

        if (user == null) {
            return Result.error("You must be logged in.");
        }
        if (!isBlank(values.get("name"))) {
            user.setName(values.get("name"));
        }
        if (!isBlank(values.get("email"))) {
            user.setEmail(values.get("email"));
        }
        if (!isBlank(values.get("organization"))) {
            user.setOrganization(values.get("organization"));
        }
        if (!isBlank(values.get("photo"))) {
            user.setPhoto(values.get("photo"));
        }

        try {
            userDAO.update(user);
            // Re-notify ProfileScreen.
            session.login(user);

            return Result.ok("Profile updated successfully.");

        } catch (SQLException e) {
            return Result.error("Update failed: " + e.getMessage());
        }
    }

    /* Security */

    public Result changePassword(String newPassword) {
        User user = session.getCurrentUser();

        if (user == null) {
            return Result.error("You must be logged in.");
        }
        user.setPassword(PasswordHasher.hash(newPassword));

        try {
            userDAO.update(user);

            return Result.ok("Password changed successfully.");

        } catch (SQLException e) {
            return Result.error("Password change failed: " + e.getMessage());
        }
    }

    /* Danger zone */

    private void deleteAccount() {
        User user = session.getCurrentUser();
        if (user == null)
            return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Account");
        confirm.setHeaderText("Delete your account?");
        confirm.setContentText("This action is permanent and cannot be undone.");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    userDAO.delete(user.getId());
                    session.logout();
                } catch (SQLException e) {
                    System.err.println("Delete account failed: " + e.getMessage());
                }
            }
        });
    }

    /* Helpers */

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    public record Result(boolean success, String message) {
        static Result ok(String message) {
            return new Result(true, message);
        }

        static Result error(String message) {
            return new Result(false, message);
        }
    }
}
