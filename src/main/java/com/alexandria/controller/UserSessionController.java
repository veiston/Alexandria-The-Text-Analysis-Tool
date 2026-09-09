package com.alexandria.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.alexandria.dao.UserDAO;
import com.alexandria.model.User;
import com.alexandria.utils.SessionStorage;

/**
 * Singleton controller that manages the current user session.
 */
public class UserSessionController {
    private static final UserSessionController INSTANCE = new UserSessionController();
    private final SessionStorage sessionStorage;
    private User currentUser;
    private final List<Consumer<User>> listeners = new ArrayList<>();

    private UserSessionController() {
        sessionStorage = new SessionStorage();
    }

    public static UserSessionController getInstance() {
        return INSTANCE;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void login(User user) {
        this.currentUser = user;
        sessionStorage.saveUserId(user.getId());
        notifyListeners();
    }

    public void logout() {
        this.currentUser = null;
        sessionStorage.clear();
        notifyListeners();
    }

    public void restore(UserDAO userDAO) throws SQLException {

        if (currentUser != null)
            return;

        Integer userId = sessionStorage.getUserId();
        if (userId == null)
            return;

        User user = userDAO.findById(userId);
        if (user == null) {
            sessionStorage.clear();
            return;
        }

        currentUser = user;
        notifyListeners();
    }

    public void addListener(Consumer<User> listener) {
        listeners.add(listener);
    }

    private void notifyListeners() {
        for (Consumer<User> listener : listeners) {
            listener.accept(currentUser);
        }
    }
}
