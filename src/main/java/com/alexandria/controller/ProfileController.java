package com.alexandria.controller;

import java.sql.SQLException;
import java.util.Map;

import com.alexandria.dao.UserDAO;
import com.alexandria.model.User;
import com.alexandria.service.LocalizationService;
import com.alexandria.utils.PasswordHasher;
import com.alexandria.view.components.shared.modal.ConfirmationAlert;
import com.alexandria.view.components.shared.modal.ErrorAlert;
import com.alexandria.view.components.shared.modal.SuccessAlert;
import com.alexandria.view.screens.ProfileScreen;

public class ProfileController {

    private final ProfileScreen profileScreen;
    private final UserDAO userDAO;
    private final UserSessionController session;

    public ProfileController(UserDAO userDAO) {
        this(null, userDAO, UserSessionController.getInstance());
    }

    public ProfileController(UserDAO userDAO, ProfileScreen profileScreen) {
        this(profileScreen, userDAO, UserSessionController.getInstance());
    }

    public ProfileController(
            ProfileScreen profileScreen,
            UserDAO userDAO,
            UserSessionController session) {

        this.profileScreen = profileScreen;
        this.userDAO = userDAO;
        this.session = session;

        if (profileScreen != null) {
            wireEvents();
        }
    }

    private void wireEvents() {
        /* Authentication */
        profileScreen.setOnLogin(values -> {
            Result result = login(
                    values.get("email"),
                    values.get("password"));

            if (!result.success()) {
                profileScreen.showLoginError(result.message());
            }
        });

        profileScreen.setOnRegister(values -> {
            Result result = register(values);

            if (!result.success()) {
                profileScreen.showRegisterError(result.message());
            }
        });

        /* Profile */
        profileScreen.setOnEditProfile(values -> {
            Result result = editProfile(values);

            if (result.success()) {
                profileScreen.closeModal();
                SuccessAlert.show(result.message());
            } else {
                profileScreen.showEditProfileError(result.message());
            }
        });

        /* Security */
        profileScreen.setOnChangePassword(password -> {
            Result result = changePassword(password);

            if (result.success()) {
                profileScreen.closeModal();
                SuccessAlert.show(result.message());
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
        LocalizationService i18n = LocalizationService.getInstance();
        String name = values.get("name");
        String email = values.get("email");
        String password = values.get("password");
        String photo = values.get("photo");
        String organization = values.get("organization");

        try {
            if (userDAO.findByEmail(email) != null) {
                return Result.error(i18n.get("sm.accountAlreadyExists"));
            }

            User user = new User(name, email, photo, organization, PasswordHasher.hash(password));
            userDAO.create(user);
            session.login(user);

            return Result.ok(i18n.get("profile.welcome"));

        } catch (SQLException e) {
            return Result.error(i18n.get("sm.registrationFailed") + " " + e.getMessage());
        }
    }

    public Result login(String email, String password) {
        LocalizationService i18n = LocalizationService.getInstance();
        try {
            User user = userDAO.findByEmail(email);

            if (user == null || !PasswordHasher.matches(password, user.getPassword())) {
                return Result.error(i18n.get("sm.incorrectEmailOrPassword"));
            }

            session.login(user);

            return Result.ok(i18n.get("profile.welcome"));

        } catch (SQLException e) {
            return Result.error(i18n.get("sm.loginFailed") + " " + e.getMessage());
        }
    }

    /* Profile */

    public Result editProfile(
            Map<String, String> values) {

        LocalizationService i18n = LocalizationService.getInstance();
        User user = session.getCurrentUser();

        if (user == null) {
            return Result.error(i18n.get("sm.mustBeLoggedIn"));
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

            return Result.ok(i18n.get("sm.profileUpdated"));

        } catch (SQLException e) {
            return Result.error(i18n.get("sm.updateFailed") + " " + e.getMessage());
        }
    }

    /* Security */

    public Result changePassword(String newPassword) {
        LocalizationService i18n = LocalizationService.getInstance();
        User user = session.getCurrentUser();

        if (user == null) {
            return Result.error(i18n.get("sm.mustBeLoggedIn"));
        }
        user.setPassword(PasswordHasher.hash(newPassword));

        try {
            userDAO.update(user);

            return Result.ok(i18n.get("sm.passwordChanged"));

        } catch (SQLException e) {
            return Result.error(i18n.get("sm.passwordChangeFailed") + " " + e.getMessage());
        }
    }

    /* Danger zone */

    private void deleteAccount() {
        User user = session.getCurrentUser();
        if (user == null)
            return;

        LocalizationService i18n = LocalizationService.getInstance();
        if (!ConfirmationAlert.show(
                i18n.get("cm.deleteAccountTitle"),
                i18n.get("cm.deleteAccountQuestion"),
                i18n.get("cm.permanentActionWarning"))) {
            return;
        }

        try {
            userDAO.delete(user.getId());
            session.logout();
        } catch (SQLException e) {
            System.err.println("Delete account failed: " + e.getMessage());
            ErrorAlert.show(i18n.get("sm.couldNotDeleteAccount"));
        }
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
