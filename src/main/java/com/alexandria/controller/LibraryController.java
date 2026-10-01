package com.alexandria.controller;

import java.sql.SQLException;
import java.io.File;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.alexandria.dao.TextDAO;
import com.alexandria.model.Text;
import com.alexandria.model.User;
import com.alexandria.model.FileType;
import com.alexandria.service.FileStorageService;
import com.alexandria.view.components.shared.modal.ErrorAlert;
import com.alexandria.view.screens.LibraryScreen;

public class LibraryController {

    private final TextDAO textDAO;
    private final LibraryScreen libraryScreen;
    private final FileStorageService fileStorageService;
    private final BiConsumer<Text, File> onOpenInAnalysis;
    private final UserSessionController session = UserSessionController.getInstance();

    public LibraryController(
            TextDAO textDAO,
            LibraryScreen libraryScreen,
            FileStorageService fileStorageService,
            BiConsumer<Text, File> onOpenInAnalysis,
            Consumer<Text> onCompare,
            Runnable onNewProject) {

        this.textDAO = textDAO;
        this.libraryScreen = libraryScreen;
        this.fileStorageService = fileStorageService;
        this.onOpenInAnalysis = onOpenInAnalysis;

        libraryScreen.setOnShown(this::loadTexts);
        libraryScreen.setOnDeleteText(this::deleteText);
        libraryScreen.setOnOpenInAnalysis(this::openInAnalysis);
        libraryScreen.setOnCompare(onCompare);
        libraryScreen.setOnNewProject(onNewProject);

        session.addListener(user -> {
            loadTexts();
        });
    }

    private void openInAnalysis(Text text) {
        File sourceFile = text.getFileType() == FileType.MANUAL ? null : fileStorageService.getFile(text.getFilePath());

        if (text.getFileType() != FileType.MANUAL && sourceFile == null) {
            ErrorAlert.show("The source file is not available on this device.");
            return;
        }

        onOpenInAnalysis.accept(text, sourceFile);
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
            Text text = textDAO.findById(id);
            textDAO.delete(id);

            if (text != null) {
                fileStorageService.deleteFile(text.getFilePath());
            }

            loadTexts();
        } catch (SQLException | java.io.IOException e) {
            System.err.println("Could not delete text: " + e.getMessage());
            ErrorAlert.show("Could not delete from library.");
        }
    }
}
