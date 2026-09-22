package com.alexandria.controller;

import com.alexandria.model.User;
import com.alexandria.service.ArchiveTermAnalysisService;
import com.alexandria.service.ArchiveTextAnalysisService;
import com.alexandria.view.components.shared.modal.ErrorAlert;
import com.alexandria.view.screens.ArchiveScreen;

import java.sql.SQLException;
import java.util.List;

public class ArchiveController {
    private final ArchiveTextAnalysisService archiveTextAnalysisService;
    private final ArchiveTermAnalysisService archiveTermAnalysisService;

    private final ArchiveScreen archiveScreen;
    private final UserSessionController session = UserSessionController.getInstance();

    public ArchiveController(ArchiveScreen archiveScreen) {
        this(new ArchiveTextAnalysisService(), new ArchiveTermAnalysisService(), archiveScreen);
    }

    ArchiveController(
            ArchiveTextAnalysisService archiveTextAnalysisService,
            ArchiveTermAnalysisService archiveTermAnalysisService,
            ArchiveScreen archiveScreen) {

        this.archiveTextAnalysisService = archiveTextAnalysisService;
        this.archiveTermAnalysisService = archiveTermAnalysisService;

        this.archiveScreen = archiveScreen;
        archiveScreen.setOnShown(this::loadAnalyses);

        archiveScreen.setOnDeleteTextAnalysis(this::deleteTextAnalysis);
        archiveScreen.setOnDeleteTermAnalysis(this::deleteTermAnalysis);
		
        session.addListener(user -> loadAnalyses());
    }

    private void loadAnalyses() {
        User user = session.getCurrentUser();

        if (user == null) {
            archiveScreen.showSignInMessage();
            return;
        }

        try {
            archiveScreen.setTextAnalyses(archiveTextAnalysisService.findAllByUserId(user.getId()));
            archiveScreen.setTermAnalyses(archiveTermAnalysisService.findAllByUserId(user.getId()));
        } catch (SQLException e) {
            System.err.println("Could not load archive: " + e.getMessage());
            ErrorAlert.show("Could not load saved analyses.");

            archiveScreen.setTextAnalyses(List.of());
            archiveScreen.setTermAnalyses(List.of());
        }
    }

    private void deleteTextAnalysis(Integer id) {
        User user = session.getCurrentUser();

        if (user == null) {
            return;
        }

        try {
            archiveTextAnalysisService.deleteById(id, user.getId());
            loadAnalyses();
        } catch (SQLException | IllegalArgumentException e) {
            System.err.println("Could not delete text analysis: " + e.getMessage());
            ErrorAlert.show("Could not delete text analysis.");
        }
    }

    private void deleteTermAnalysis(Integer id) {
        User user = session.getCurrentUser();

        if (user == null) {
            return;
        }

        try {
            archiveTermAnalysisService.deleteById(id, user.getId());
            loadAnalyses();
        } catch (SQLException | IllegalArgumentException e) {
            System.err.println("Could not delete term analysis: " + e.getMessage());
            ErrorAlert.show("Could not delete term analysis.");
        }
    }
}
