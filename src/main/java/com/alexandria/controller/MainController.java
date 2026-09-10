package com.alexandria.controller;

import com.alexandria.dao.TextDAO;
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
        configureProject();
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

    private void configureProject() {
        ProjectController projectController = new ProjectController(new TextDAO());

        mainView.setOnProjectCreated(created -> {
            ProjectController.Result result = projectController.createProject(created);

            if (!result.success()) {
                mainView.showProjectError(result.message());
                return;
            }

            mainView.closeProjectModal();

            switch (created.destination()) {
                case ANALYSE -> mainView.navigateTo(Route.ANALYZE);
                case COMPARE -> mainView.navigateTo(Route.COMPARE);
            }
        });
    }

    public MainView getView() {
        return mainView;
    }
}