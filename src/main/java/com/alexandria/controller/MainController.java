package com.alexandria.controller;

import com.alexandria.dao.UserDAO;
import com.alexandria.view.MainView;
import com.alexandria.view.router.Route;
import com.alexandria.view.screens.ProfileScreen;

public class MainController {

    private final MainView mainView;
    private final UserDAO userDAO;

    private final UserSessionController session = UserSessionController.getInstance();

    public MainController() {
        userDAO = new UserDAO();
        restoreSession();

        mainView = new MainView();
        configureProfile();
    }

    private void restoreSession() {
        try {
            session.restore(userDAO);
        } catch (Exception e) {
            System.err.println("Could not restore session: " + e.getMessage());
        }
    }

    private void configureProfile() {
        ProfileScreen profileScreen = (ProfileScreen) Route.PROFILE.createScreen();
        new ProfileController(userDAO, profileScreen);
    }

    public MainView getView() {
        return mainView;
    }
}
