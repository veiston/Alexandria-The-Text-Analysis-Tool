package com.alexandria.controller;

import com.alexandria.dao.TextDAO;
import com.alexandria.dao.UserDAO;
import com.alexandria.model.Text;
import com.alexandria.service.PdfService;
import com.alexandria.service.SearchService;
import com.alexandria.service.TermAnalysisService;
import com.alexandria.service.TextAnalysisService;
import com.alexandria.view.MainView;
import com.alexandria.view.components.side_navbar.new_project.NewProjectModal;
import com.alexandria.view.router.Route;
import com.alexandria.view.screens.AnalyseScreen;
import com.alexandria.view.screens.ArchiveScreen;
import com.alexandria.view.screens.ProfileScreen;

import javafx.concurrent.Task;

import java.io.File;
import java.util.List;

public class MainController {

    private final MainView mainView;
    private final UserDAO userDAO;
    private final UserSessionController session = UserSessionController.getInstance();
    private final AnalyseController analyseController;

    public MainController() {
        userDAO = new UserDAO();
        restoreSession();

        analyseController = new AnalyseController(
                new SearchService(),
                new TermAnalysisService(),
                new TextAnalysisService());

        mainView = new MainView();
        configureProfile();
        configureArchive();
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
        ProjectController projectController = new ProjectController(new TextDAO(), new PdfService());

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
                routeToDestination(
                        result.text(),
                        result.pageOffsets(),
                        result.sourceFile(),
                        created.destination());
            });

            task.setOnFailed(e -> {
                mainView.setNewProjectLoading(false);

                Throwable error = task.getException();
                mainView.showProjectError(
                        "Unexpected error: "
                                + (error != null ? error.getMessage() : "unknown"));
            });

            Thread worker = new Thread(task, "new-project-worker");
            worker.setDaemon(true);
            worker.start();
        });
    }

    private void configureArchive() {
        ArchiveScreen archiveScreen = (ArchiveScreen) Route.ARCHIVE.createScreen();
        new ArchiveController(archiveScreen);
    }

    private void routeToDestination(
            Text text,
            List<Integer> pageOffsets,
            File sourceFile,
            NewProjectModal.Destination destination) {
        switch (destination) {
            case ANALYSE -> openAnalysis(text, pageOffsets, sourceFile);
            case COMPARE -> mainView.navigateTo(Route.COMPARE);
        }
    }

    private void openAnalysis(
            Text text,
            List<Integer> pageOffsets,
            File sourceFile) {

        AnalyseScreen analyseScreen = (AnalyseScreen) Route.ANALYZE.createScreen();

        analyseController.configureScreen(
                analyseScreen,
                text,
                pageOffsets,
                sourceFile);

        analyseScreen.setOnSaveAnalysis(() -> {
            // TODO: saving preview + confirm submission.
            // No persistence concept for the final analysis exists yet.
        });

        mainView.navigateTo(Route.ANALYZE);
    }

    public MainView getView() {
        return mainView;
    }
}
