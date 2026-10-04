package com.alexandria.controller;

import com.alexandria.dao.TermComparisonDAO;
import com.alexandria.dao.TermComparisonTextDAO;
import com.alexandria.dao.TextComparisonDAO;
import com.alexandria.dao.TextComparisonTextDAO;
import com.alexandria.dao.TextDAO;
import com.alexandria.model.TermComparison;
import com.alexandria.model.TermComparisonText;
import com.alexandria.model.Text;
import com.alexandria.model.TextComparison;
import com.alexandria.model.TextComparisonText;
import com.alexandria.model.User;
import com.alexandria.service.ArchiveTermAnalysisService;
import com.alexandria.service.ArchiveTextAnalysisService;
import com.alexandria.service.analysis.TermComparisonResult;
import com.alexandria.service.analysis.TextComparisonResult;
import com.alexandria.utils.JsonMapper;
import com.alexandria.model.ArchiveComparison;
import com.alexandria.view.components.shared.modal.ErrorAlert;
import com.alexandria.view.screens.ArchiveScreen;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ArchiveController {

    private final ArchiveTextAnalysisService archiveTextAnalysisService;
    private final ArchiveTermAnalysisService archiveTermAnalysisService;

    private final TextComparisonDAO textComparisonDAO;
    private final TextComparisonTextDAO textComparisonTextDAO;
    private final TermComparisonDAO termComparisonDAO;
    private final TermComparisonTextDAO termComparisonTextDAO;
    private final TextDAO textDAO;

    private final ArchiveScreen archiveScreen;
    private final UserSessionController session = UserSessionController.getInstance();

    /**
     * Normal constructor used by the application.
     */
    public ArchiveController(ArchiveScreen archiveScreen) {
        this(
                new ArchiveTextAnalysisService(),
                new ArchiveTermAnalysisService(),
                new TextComparisonDAO(),
                new TextComparisonTextDAO(),
                new TermComparisonDAO(),
                new TermComparisonTextDAO(),
                new TextDAO(),
                archiveScreen);
    }

    /**
     * Constructor used for testing.
     *
     * Allows DAOs and services to be replaced with Mockito mocks.
     */
    ArchiveController(
            ArchiveTextAnalysisService archiveTextAnalysisService,
            ArchiveTermAnalysisService archiveTermAnalysisService,
            TextComparisonDAO textComparisonDAO,
            TextComparisonTextDAO textComparisonTextDAO,
            TermComparisonDAO termComparisonDAO,
            TermComparisonTextDAO termComparisonTextDAO,
            TextDAO textDAO,
            ArchiveScreen archiveScreen) {

        this.archiveTextAnalysisService = archiveTextAnalysisService;
        this.archiveTermAnalysisService = archiveTermAnalysisService;

        this.textComparisonDAO = textComparisonDAO;
        this.textComparisonTextDAO = textComparisonTextDAO;
        this.termComparisonDAO = termComparisonDAO;
        this.termComparisonTextDAO = termComparisonTextDAO;
        this.textDAO = textDAO;

        this.archiveScreen = archiveScreen;

        archiveScreen.setOnShown(this::loadAnalyses);

        archiveScreen.setOnDeleteTextAnalysis(this::deleteTextAnalysis);
        archiveScreen.setOnDeleteTermAnalysis(this::deleteTermAnalysis);
        archiveScreen.setOnDeleteTextComparison(this::deleteTextComparison);
        archiveScreen.setOnDeleteTermComparison(this::deleteTermComparison);

        session.addListener(user -> loadAnalyses());
    }

    void loadAnalyses() {
        User user = session.getCurrentUser();

        if (user == null) {
            archiveScreen.showSignInMessage();
            return;
        }

        try {
            archiveScreen.setTextAnalyses(
                    archiveTextAnalysisService.findAllByUserId(user.getId()));

            archiveScreen.setTermAnalyses(
                    archiveTermAnalysisService.findAllByUserId(user.getId()));

            archiveScreen.setTextComparisons(
                    loadTextComparisons(user.getId()));

            archiveScreen.setTermComparisons(
                    loadTermComparisons(user.getId()));

        } catch (SQLException | RuntimeException e) {
            System.err.println("Could not load archive: " + e.getMessage());
            ErrorAlert.show("Could not load saved analyses.");

            archiveScreen.setTextAnalyses(List.of());
            archiveScreen.setTermAnalyses(List.of());
            archiveScreen.setTextComparisons(List.of());
            archiveScreen.setTermComparisons(List.of());
        }
    }

    private List<ArchiveComparison> loadTextComparisons(int userId)
            throws SQLException {

        List<ArchiveComparison> comparisons = new ArrayList<>();

        for (TextComparison comparison : textComparisonDAO.findAllByUserId(userId)) {

            TextComparisonResult result;

            try {
                result = JsonMapper.fromJson(
                        comparison.getComparisonData(),
                        TextComparisonResult.class);
            } catch (RuntimeException e) {
                logSkipped("text comparison", comparison.getId(), e);
                continue;
            }

            List<Integer> textIds = new ArrayList<>();

            for (TextComparisonText link : textComparisonTextDAO.findAllByComparisonId(
                    comparison.getId())) {

                textIds.add(link.getTextId());
            }

            List<Text> texts = findTexts(textIds);

            comparisons.add(new ArchiveComparison(
                    comparison.getId(),
                    joinTitles(texts),
                    joinFileNames(texts),
                    null,
                    comparison.getCreatedAt(),
                    result,
                    null));
        }

        return comparisons;
    }

    private List<ArchiveComparison> loadTermComparisons(int userId)
            throws SQLException {

        List<ArchiveComparison> comparisons = new ArrayList<>();

        for (TermComparison comparison : termComparisonDAO.findAllByUserId(userId)) {

            TermComparisonResult result;

            try {
                result = JsonMapper.fromJson(
                        comparison.getComparisonData(),
                        TermComparisonResult.class);
            } catch (RuntimeException e) {
                logSkipped("term comparison", comparison.getId(), e);
                continue;
            }

            List<Integer> textIds = new ArrayList<>();

            for (TermComparisonText link : termComparisonTextDAO.findAllByComparisonId(
                    comparison.getId())) {

                textIds.add(link.getTextId());
            }

            List<Text> texts = findTexts(textIds);

            comparisons.add(new ArchiveComparison(
                    comparison.getId(),
                    joinTitles(texts),
                    joinFileNames(texts),
                    comparison.getTerm(),
                    comparison.getCreatedAt(),
                    null,
                    result));
        }

        return comparisons;
    }

    /**
     * One unreadable saved row must not blank the whole archive.
     * Log the real cause and carry on with the remaining rows.
     */
    private void logSkipped(String kind, Integer id, RuntimeException e) {
        Throwable cause = e.getCause() != null ? e.getCause() : e;

        System.err.println(
                "Skipping unreadable saved " + kind + " " + id + ": " + cause.getMessage());
    }

    private List<Text> findTexts(List<Integer> textIds)
            throws SQLException {

        List<Text> texts = new ArrayList<>();

        for (Integer textId : textIds) {
            Text text = textDAO.findById(textId);

            if (text != null) {
                texts.add(text);
            }
        }

        return texts;
    }

    private String joinTitles(List<Text> texts) {
        List<String> titles = new ArrayList<>();

        for (Text text : texts) {
            boolean hasTitle = text.getTitle() != null &&
                    !text.getTitle().isBlank();

            titles.add(
                    hasTitle
                            ? text.getTitle()
                            : text.getFileName());
        }

        return String.join(" vs ", titles);
    }

    private String joinFileNames(List<Text> texts) {
        List<String> names = new ArrayList<>();

        for (Text text : texts) {
            names.add(text.getFileName());
        }

        return String.join(" · ", names);
    }

    void deleteTextAnalysis(Integer id) {
        User user = session.getCurrentUser();

        if (user == null) {
            return;
        }

        try {
            archiveTextAnalysisService.deleteById(id, user.getId());
            loadAnalyses();

        } catch (SQLException | IllegalArgumentException e) {
            System.err.println(
                    "Could not delete text analysis: " + e.getMessage());

            ErrorAlert.show("Could not delete text analysis.");
        }
    }

    void deleteTermAnalysis(Integer id) {
        User user = session.getCurrentUser();

        if (user == null) {
            return;
        }

        try {
            archiveTermAnalysisService.deleteById(id, user.getId());
            loadAnalyses();

        } catch (SQLException | IllegalArgumentException e) {
            System.err.println(
                    "Could not delete term analysis: " + e.getMessage());

            ErrorAlert.show("Could not delete term analysis.");
        }
    }

    void deleteTextComparison(Integer id) {
        User user = session.getCurrentUser();

        if (user == null) {
            return;
        }

        try {
            TextComparison comparison = textComparisonDAO.findById(id);

            if (comparison == null ||
                    !Objects.equals(user.getId(), comparison.getUserId())) {

                throw new IllegalArgumentException(
                        "Text comparison does not belong to the provided user.");
            }

            for (TextComparisonText link : textComparisonTextDAO.findAllByComparisonId(id)) {

                textComparisonTextDAO.delete(
                        id,
                        link.getTextId());
            }

            textComparisonDAO.delete(id);
            loadAnalyses();

        } catch (SQLException | IllegalArgumentException e) {
            System.err.println(
                    "Could not delete text comparison: " + e.getMessage());

            ErrorAlert.show("Could not delete text comparison.");
        }
    }

    void deleteTermComparison(Integer id) {
        User user = session.getCurrentUser();

        if (user == null) {
            return;
        }

        try {
            TermComparison comparison = termComparisonDAO.findById(id);

            if (comparison == null ||
                    !Objects.equals(user.getId(), comparison.getUserId())) {

                throw new IllegalArgumentException(
                        "Term comparison does not belong to the provided user.");
            }

            for (TermComparisonText link : termComparisonTextDAO.findAllByComparisonId(id)) {

                termComparisonTextDAO.delete(
                        id,
                        link.getTextId());
            }

            termComparisonDAO.delete(id);
            loadAnalyses();

        } catch (SQLException | IllegalArgumentException e) {
            System.err.println(
                    "Could not delete term comparison: " + e.getMessage());

            ErrorAlert.show("Could not delete term comparison.");
        }
    }
}