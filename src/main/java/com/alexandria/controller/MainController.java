package com.alexandria.controller;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import com.alexandria.dao.TextDAO;
import com.alexandria.dao.UserDAO;
import com.alexandria.model.FileType;
import com.alexandria.model.Text;
import com.alexandria.service.FileStorageService;
import com.alexandria.service.PdfService;
import com.alexandria.service.SearchService;
import com.alexandria.service.TermAnalysisService;
import com.alexandria.service.TextAnalysisService;
import com.alexandria.utils.UserGuideSettings;
import com.alexandria.view.MainView;
import com.alexandria.view.components.shared.document.highlight.TextPaginator;
import com.alexandria.view.components.shared.modal.ErrorAlert;
import com.alexandria.view.components.side_navbar.new_project.NewProjectModal;
import com.alexandria.view.components.user_guide.UserGuideTour;
import com.alexandria.view.components.user_guide.UserGuideTourData;
import com.alexandria.view.router.Route;
import com.alexandria.view.screens.AnalyseScreen;
import com.alexandria.view.screens.ArchiveScreen;
import com.alexandria.view.screens.CompareScreen;
import com.alexandria.view.screens.LibraryScreen;
import com.alexandria.view.screens.ProfileScreen;

import javafx.application.Platform;
import javafx.concurrent.Task;

public class MainController {

    private final MainView mainView;
    private final UserDAO userDAO;
    private final UserSessionController session = UserSessionController.getInstance();
    private final AnalyseController analyseController;
    private final CompareController compareController;
    private final ArchiveController archiveController;
    private final LibraryController libraryController;
    private CompareScreen compareScreen;
    private Text firstComparisonText;
    private File firstComparisonSourceFile;
    private final PdfService pdfService = new PdfService();

    public MainController() {
        userDAO = new UserDAO();
        restoreSession();

        mainView = new MainView();

        analyseController = new AnalyseController(
                new SearchService(),
                new TermAnalysisService(),
                new TextAnalysisService());

        configureProfile();
        archiveController = configureArchive();
        compareController = configureComparison();
        libraryController = configureLibrary();
        configureProject();
        configureUserGuideTour();

        openInitialRoute();
    }

    public void startUserGuideIfNeeded() {
        if (UserGuideSettings.isShownOnStartup()) {
            Platform.runLater(() -> {
                mainView.startUserGuide();
            });
        }
    }

    private void restoreSession() {
        try {
            session.restore(userDAO);
        } catch (Exception e) {
            System.err.println("Could not restore session: " + e.getMessage());
        }
    }

    private void openInitialRoute() {
        mainView.navigateTo(session.isLoggedIn() ? Route.LIBRARY : Route.PROFILE);
    }

    private void configureProfile() {
        ProfileScreen profileScreen = (ProfileScreen) Route.PROFILE.createScreen();
        new ProfileController(userDAO, profileScreen);
    }

    private ArchiveController configureArchive() {
        ArchiveScreen archiveScreen = (ArchiveScreen) Route.ARCHIVE.createScreen();
        return new ArchiveController(archiveScreen);
    }

    private CompareController configureComparison() {
        compareScreen = (CompareScreen) Route.COMPARE.createScreen();
        CompareController controller = new CompareController();
        compareScreen.setOnSaveFindings(() -> {
            CompareController.SaveOutcome outcome = controller.saveFindings(compareScreen.getTrackedSearchTerms());
            if (outcome.success()) {
                compareScreen.showSaved(outcome.savedCount());
                archiveController.loadAnalyses();
            } else {
                ErrorAlert.show(outcome.message());
            }
        });
        compareScreen.setOnSearch(term -> showComparisonSearch(controller, term));
        compareScreen.setOnPreviousMatch(compareScreen::showPreviousSearchMatch);
        compareScreen.setOnNextMatch(compareScreen::showNextSearchMatch);
        return controller;
    }

    private void showComparisonSearch(CompareController controller, String term) {
        CompareController.MultiSearchOutcome outcome = controller.search(
                term, compareScreen.getSearchSettings());
        if (!outcome.success()) {
            ErrorAlert.show(outcome.message());
            return;
        }
        List<Text> texts = controller.getCurrentTexts();
        if (texts.size() < 2) {
            return;
        }
        compareScreen.showSearchResults(
                outcome.matches(), comparisonTextId(texts.get(0), 0), comparisonTextId(texts.get(1), 1));
    }

    private LibraryController configureLibrary() {
        LibraryScreen libraryScreen = (LibraryScreen) Route.LIBRARY.createScreen();
        return new LibraryController(
                new TextDAO(),
                libraryScreen,
                new FileStorageService(),
                this::openLibraryText,
                this::openLibraryComparison,
                this::addNewComparisonText,
                mainView::showNewProjectModal);
    }

    // Now taking PDF page breaks into account, instead of just processing it as a
    // long string
    private void openLibraryText(Text text, File sourceFile) {
        List<Integer> pageOffsets = TextPaginator.paginate(text.getContent(), TextPaginator.CHARS_PER_PAGE);
        if (text.getFileType() == FileType.PDF && sourceFile != null) {
            try {
                pageOffsets = pdfService.extractTextWithPageBoundaries(sourceFile).pageOffsets();
            } catch (Exception e) {
                System.out.println("Failed to extract PDF page breaks. ERROR: " + e);
            }
        }
        openAnalysis(text, pageOffsets, sourceFile);
    }

    private void configureUserGuideTour() {
        mainView.addEventHandler(UserGuideTour.OPEN_ANALYSIS_EVENT, event -> {
            Text tourText = new Text(
                    null,
                    UserGuideTourData.PROJECT_TITLE,
                    UserGuideTourData.FILE_NAME,
                    UserGuideTourData.FILE_TYPE,
                    UserGuideTourData.TEXT);

            mainView.closeProjectModal();

            openAnalysis(tourText, List.of(), null);
        });

        mainView.addEventHandler(UserGuideTour.CLOSE_ANALYSIS_EVENT,
                event -> ((AnalyseScreen) Route.ANALYZE.createScreen()).clearAnalysis());
        mainView.addEventHandler(UserGuideTour.CLOSE_ARCHIVE_EVENT,
                event -> archiveController.loadAnalyses());
    }

    private void configureProject() {
        ProjectController projectController = new ProjectController(new TextDAO(), pdfService);

        mainView.setOnProjectCreated(created -> {
            boolean addingSecondComparisonText = created.addingSecondComparisonText();
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
                libraryController.loadTexts();

                if (addingSecondComparisonText) {
                    openComparisonWithNewSecondText(result.text(), result.sourceFile());
                } else {
                    routeToDestination(
                            result.text(),
                            result.pageOffsets(),
                            result.sourceFile(),
                            created.destination());
                }
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

    private void routeToDestination(
            Text text,
            List<Integer> pageOffsets,
            File sourceFile,
            NewProjectModal.Destination destination) {
        switch (destination) {
            case ANALYSE -> openAnalysis(text, pageOffsets, sourceFile);
            case COMPARE -> openNewProjectComparison(text, sourceFile);
        }
    }

    private void openNewProjectComparison(Text firstText, File firstSourceFile) {
        firstComparisonText = firstText;
        firstComparisonSourceFile = firstSourceFile;

        if (session.isLoggedIn()) {
            mainView.navigateTo(Route.LIBRARY);
            libraryController.startComparison(firstText);
        } else {
            mainView.showSecondComparisonTextModal(comparisonTextTitle(firstText));
        }
    }

    private void addNewComparisonText(Text firstText, File firstSourceFile) {
        firstComparisonText = firstText;
        firstComparisonSourceFile = firstSourceFile;
        mainView.showSecondComparisonTextModal(comparisonTextTitle(firstText));
    }

    private void openComparisonWithNewSecondText(Text secondText, File secondSourceFile) {
        if (firstComparisonText == null) {
            ErrorAlert.show("Choose the first text for comparison.");
            return;
        }

        openLibraryComparison(
                List.of(firstComparisonText, secondText),
                Arrays.asList(firstComparisonSourceFile, secondSourceFile));
    }

    private String comparisonTextTitle(Text text) {
        if (text.getTitle() != null && !text.getTitle().isBlank()) {
            return text.getTitle();
        }
        return text.getFileName();
    }

    private void openLibraryComparison(List<Text> texts, List<File> sourceFiles) {
        CompareController.ComparisonTextsOutcome outcome = compareController.openTexts(texts, sourceFiles);

        if (!outcome.success()) {
            ErrorAlert.show(outcome.message());
            return;
        }

        firstComparisonText = null;
        firstComparisonSourceFile = null;

        List<Text> openedTexts = outcome.texts();
        List<File> openedFiles = compareController.getCurrentFiles();

        compareScreen.loadTexts(
                openedTexts.get(0),
                openedFiles.size() > 0 ? openedFiles.get(0) : null,
                openedTexts.get(1),
                openedFiles.size() > 1 ? openedFiles.get(1) : null);

        CompareController.TextComparisonOutcome comparison = compareController
                .compareTexts(CompareController.COMMON_WORDS_LIMIT);
        if (comparison.success()) {
            compareScreen.setTextComparison(comparison.result());
        } else {
            ErrorAlert.show(comparison.message());
        }

        mainView.navigateTo(Route.COMPARE);
    }

    private int comparisonTextId(Text text, int index) {
        return text.getId() == null ? -index - 1 : text.getId();
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
            AnalyseController.SaveOutcome outcome = analyseController.saveAnalysis();

            if (outcome.success()) {
                analyseScreen.showSaved(outcome.savedCount());
            } else {
                System.err.println(outcome.message());
            }
        });

        mainView.navigateTo(Route.ANALYZE);
    }

    public MainView getView() {
        return mainView;
    }
}