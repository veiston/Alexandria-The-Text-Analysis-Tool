package com.alexandria.controller;

import java.sql.SQLException;
import java.util.List;
import java.util.function.Consumer;

import com.alexandria.dao.TextDAO;
import com.alexandria.model.Text;
import com.alexandria.model.User;
import com.alexandria.view.components.shared.modal.ErrorAlert;
import com.alexandria.view.screens.LibraryScreen;

public class LibraryController {

    private final TextDAO textDAO;
    private final LibraryScreen libraryScreen;
    private final UserSessionController session = UserSessionController.getInstance();

    public LibraryController(
            TextDAO textDAO,
            LibraryScreen libraryScreen,
            Consumer<Text> onOpenInAnalysis,
            Consumer<Text> onCompare,
            Runnable onNewProject) {

        this.textDAO = textDAO;
        this.libraryScreen = libraryScreen;

        libraryScreen.setOnShown(this::loadTexts);
        libraryScreen.setOnDeleteText(this::deleteText);
        libraryScreen.setOnOpenInAnalysis(onOpenInAnalysis);
        libraryScreen.setOnCompare(onCompare);
        libraryScreen.setOnNewProject(onNewProject);

        session.addListener(user -> {
            loadTexts();
        });
    }

    public LibraryController(
            LibraryScreen libraryScreen,
            Consumer<Text> onOpenInAnalysis,
            Consumer<Text> onCompare,
            Runnable onNewProject) {

        this(new TextDAO(), libraryScreen, onOpenInAnalysis, onCompare, onNewProject);
    }

    void loadTexts() {
        User user = session.getCurrentUser();

        // Guest users work in-memory; require an authenticated account to access saved library entries
        if (user == null) {
            libraryScreen.showSignInMessage();
            return;
        }

        try {
            List<Text> texts = textDAO.findAllByUserId(user.getId());
            libraryScreen.setTexts(texts);
        } catch (SQLException e) {
            System.err.println("Could not load library: " + e.getMessage());
            ErrorAlert.show("Could not load saved texts.");
            libraryScreen.setTexts(List.of());
        }
    }

    void deleteText(Integer id) {
        User user = session.getCurrentUser();

        if (user == null || id == null) {
            return;
        }

        try {
            textDAO.delete(id);
            loadTexts();
        } catch (SQLException e) {
            System.err.println("Could not delete text: " + e.getMessage());
            ErrorAlert.show("Could not delete from library.");
        }
    }
}
