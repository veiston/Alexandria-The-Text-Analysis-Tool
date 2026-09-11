package com.alexandria.controller;

import com.alexandria.dao.TextDAO;
import com.alexandria.dao.UserDAO;
import com.alexandria.model.Text;
import com.alexandria.view.MainView;
import com.alexandria.view.router.Route;
import com.alexandria.view.screens.ProfileScreen;
import com.alexandria.view.components.side_navbar.new_project.NewProjectModal.Destination;

import javafx.concurrent.Task;

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
            mainView.setNewProjectLoading(true);

            Task<ProjectController.Result> task = new Task<>() {
                @Override
                protected ProjectController.Result call() {
                    return projectController.createProject(created);
                }
            };

            task.setOnSucceeded(e -> {
                mainView.setNewProjectLoading(false);
                ProjectController.Result result = task.getValue();

                if (!result.success()) {
                    mainView.showProjectError(result.message());
                    return;
                }

                mainView.closeProjectModal();
                routeToDestination(result.text(), created.destination());
            });

            task.setOnFailed(e -> {
                mainView.setNewProjectLoading(false);
                Throwable error = task.getException();
                mainView.showProjectError("Unexpected error: " + (error != null ? error.getMessage() : "unknown"));
            });

            Thread worker = new Thread(task, "new-project-worker");
            worker.setDaemon(true);
            worker.start();
        });
    }

    private void routeToDestination(Text text, Destination destination) {
        switch (destination) {
            case ANALYSE -> {
                // TODO: once AnalyseController is wired here, call
                // analyseController.openText(text) BEFORE navigating, so the
                // screen shows the text that was just created.
                mainView.navigateTo(Route.ANALYZE);
            }
            case COMPARE -> {
                // TODO: CompareController once built, needs the same pattern — hand `text` to
                // the compare controller before navigating.
                mainView.navigateTo(Route.COMPARE);
            }
        }
    }

    public MainView getView() {
        return mainView;
    }
}