package com.alexandria.view.screens;

import com.alexandria.model.ArchiveTermAnalysis;
import com.alexandria.model.ArchiveTextAnalysis;
import com.alexandria.service.LocalizationService;
import com.alexandria.view.components.archive_screen.ArchiveAnalysisCard;
import com.alexandria.view.components.archive_screen.ArchiveAnalysisModal;
import com.alexandria.model.ArchiveComparison;
import com.alexandria.view.components.shared.EmptyState;
import com.alexandria.view.components.shared.SearchInput;
import com.alexandria.view.components.shared.toggle.Toggle;
import com.alexandria.view.components.shared.modal.ConfirmationAlert;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.function.Consumer;

public class ArchiveScreen extends StackPane {

    private final Toggle analysisFilter;
    private final SearchInput searchInput;
    private final FlowPane analysisCards = new FlowPane(16, 16);
    private final StackPane contentArea = new StackPane();
    private final BorderPane archiveLayout = new BorderPane();
    private final VBox archiveBody;
    private final ArchiveAnalysisModal analysisModal = new ArchiveAnalysisModal();
    private final Label title = new Label();
    private final Label subtitle = new Label();

    private List<ArchiveTextAnalysis> textAnalyses = List.of();
    private List<ArchiveTermAnalysis> termAnalyses = List.of();
    private List<ArchiveComparison> textComparisons = List.of();
    private List<ArchiveComparison> termComparisons = List.of();

    private Consumer<Integer> onDeleteTextAnalysis = id -> {
    };
    private Consumer<Integer> onDeleteTermAnalysis = id -> {
    };
    private Consumer<Integer> onDeleteTextComparison = id -> {
    };
    private Consumer<Integer> onDeleteTermComparison = id -> {
    };
    private Runnable onShown = () -> {
    };

    public ArchiveScreen() {
        LocalizationService i18n = LocalizationService.getInstance();
        analysisFilter = new Toggle(
                i18n.get("textAnalysis"),
                i18n.get("termAnalysis"),
                i18n.get("textComparison"),
                i18n.get("termComparison"));
        searchInput = new SearchInput(i18n.get("archive.search"));
        archiveBody = buildBody();

        getStyleClass().add("content-screen");
        searchInput.setMaxWidth(Double.MAX_VALUE);

        archiveLayout.setTop(buildHeader());
        archiveLayout.setCenter(archiveBody);
        archiveLayout.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        getChildren().addAll(archiveLayout, analysisModal);
        StackPane.setAlignment(archiveLayout, Pos.CENTER);
        StackPane.setAlignment(analysisModal, Pos.CENTER);
        analysisModal.prefWidthProperty().bind(widthProperty());
        analysisModal.prefHeightProperty().bind(heightProperty());

        configureActions();
        refreshText();
        LocalizationService.getInstance().addListener(locale -> refreshText());

        sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                onShown.run();
            }
        });
    }

    public void refreshText() {
        LocalizationService i18n = LocalizationService.getInstance();
        title.setText(i18n.get("archive"));
        subtitle.setText(i18n.get("archive.subtitle"));
        analysisFilter.setLabels(
                i18n.get("textAnalysis"),
                i18n.get("termAnalysis"),
                i18n.get("textComparison"),
                i18n.get("termComparison"));
        searchInput.setPromptText(i18n.get("archive.search"));
        showStatistics();
    }

    private VBox buildHeader() {
        title.getStyleClass().add("heading-xl");

        subtitle.getStyleClass().add("text-muted");
        subtitle.getStyleClass().add("archive-subtitle");

        VBox header = new VBox(6, title, subtitle);
        header.getStyleClass().add("content-screen-header");
        return header;
    }

    private VBox buildBody() {
        VBox body = new VBox(contentArea);
        body.getStyleClass().add("content-screen-body");
        VBox.setVgrow(contentArea, Priority.ALWAYS);
        return body;
    }

    private void configureActions() {
        analysisFilter.setOnToggle(index -> showStatistics());
        searchInput.textProperty().addListener((observable, oldText, newText) -> showStatistics());
    }

    public void setTextAnalyses(List<ArchiveTextAnalysis> textAnalyses) {
        this.textAnalyses = textAnalyses == null ? List.of() : List.copyOf(textAnalyses);
        showStatistics();
    }

    public void setTermAnalyses(List<ArchiveTermAnalysis> termAnalyses) {
        this.termAnalyses = termAnalyses == null ? List.of() : List.copyOf(termAnalyses);
        showStatistics();
    }

    public void setTextComparisons(List<ArchiveComparison> textComparisons) {
        this.textComparisons = textComparisons == null ? List.of() : List.copyOf(textComparisons);
        showStatistics();
    }

    public void setTermComparisons(List<ArchiveComparison> termComparisons) {
        this.termComparisons = termComparisons == null ? List.of() : List.copyOf(termComparisons);
        showStatistics();
    }

    public void setOnDeleteTextAnalysis(Consumer<Integer> handler) {
        onDeleteTextAnalysis = handler == null ? id -> {
        } : handler;
    }

    public void setOnDeleteTermAnalysis(Consumer<Integer> handler) {
        onDeleteTermAnalysis = handler == null ? id -> {
        } : handler;
    }

    public void setOnDeleteTextComparison(Consumer<Integer> handler) {
        onDeleteTextComparison = handler == null ? id -> {
        } : handler;
    }

    public void setOnDeleteTermComparison(Consumer<Integer> handler) {
        onDeleteTermComparison = handler == null ? id -> {
        } : handler;
    }

    public void setOnShown(Runnable handler) {
        onShown = handler == null ? () -> {
        } : handler;
    }

    public void showSignInMessage() {
        LocalizationService i18n = LocalizationService.getInstance();
        EmptyState emptyState = new EmptyState(
                i18n.get("archive.signInTitle"),
                i18n.get("archive.signInSubtitle"));
        archiveLayout.setCenter(emptyState);
        BorderPane.setAlignment(emptyState, Pos.CENTER);
    }

    private void showStatistics() {
        archiveLayout.setCenter(archiveBody);

        analysisCards.getChildren().clear();
        LocalizationService i18n = LocalizationService.getInstance();

        if (analysisFilter.getSelectedIndex() == 0) {
            for (ArchiveTextAnalysis analysis : textAnalyses) {
                if (matchesSearch(
                        analysis.getProjectTitle(),
                        analysis.getSourceFileName())) {
                    ArchiveAnalysisCard card = new ArchiveAnalysisCard(analysis);
                    card.getDeleteButton().setOnAction(event -> {
                        if (ConfirmationAlert.show(
                                i18n.get("cm.deleteAnalysisTitle"),
                                i18n.get("cm.deleteAnalysisQuestion"),
                                i18n.get("cm.permanentActionWarning"))) {
                            onDeleteTextAnalysis.accept(analysis.getId());
                        }
                    });
                    card.getOpenButton().setOnAction(event -> analysisModal.showTextAnalysis(analysis));
                    analysisCards.getChildren().add(card);
                }
            }
        }

        if (analysisFilter.getSelectedIndex() == 1) {
            for (ArchiveTermAnalysis analysis : termAnalyses) {
                if (matchesSearch(
                        analysis.getProjectTitle(),
                        analysis.getSourceFileName(),
                        analysis.getTerm())) {
                    ArchiveAnalysisCard card = new ArchiveAnalysisCard(analysis);
                    card.getDeleteButton().setOnAction(event -> {
                        if (ConfirmationAlert.show(
                                i18n.get("cm.deleteAnalysisTitle"),
                                i18n.get("cm.deleteAnalysisQuestion"),
                                i18n.get("cm.permanentActionWarning"))) {
                            onDeleteTermAnalysis.accept(analysis.getId());
                        }
                    });
                    card.getOpenButton().setOnAction(event -> analysisModal.showTermAnalysis(analysis));
                    analysisCards.getChildren().add(card);
                }
            }
        }

        if (analysisFilter.getSelectedIndex() == 2) {
            for (ArchiveComparison comparison : textComparisons) {
                if (matchesSearch(comparison.title(), comparison.sources())) {
                    ArchiveAnalysisCard card = new ArchiveAnalysisCard(comparison);
                    card.getDeleteButton().setOnAction(event -> {
                        if (ConfirmationAlert.show(
                                i18n.get("cm.deleteComparisonTitle"),
                                i18n.get("cm.deleteComparisonQuestion"),
                                i18n.get("cm.permanentActionWarning"))) {
                            onDeleteTextComparison.accept(comparison.id());
                        }
                    });
                    card.getOpenButton().setOnAction(event -> analysisModal.showTextComparison(comparison));
                    analysisCards.getChildren().add(card);
                }
            }
        }

        if (analysisFilter.getSelectedIndex() == 3) {
            for (ArchiveComparison comparison : termComparisons) {
                if (matchesSearch(comparison.title(), comparison.sources(), comparison.term())) {
                    ArchiveAnalysisCard card = new ArchiveAnalysisCard(comparison);
                    card.getDeleteButton().setOnAction(event -> {
                        if (ConfirmationAlert.show(
                                i18n.get("cm.deleteComparisonTitle"),
                                i18n.get("cm.deleteComparisonQuestion"),
                                i18n.get("cm.permanentActionWarning"))) {
                            onDeleteTermComparison.accept(comparison.id());
                        }
                    });
                    card.getOpenButton().setOnAction(event -> analysisModal.showTermComparison(comparison));
                    analysisCards.getChildren().add(card);
                }
            }
        }

        StackPane statisticsContent = new StackPane();
        statisticsContent.getStyleClass().add("archive-content");

        if (analysisCards.getChildren().isEmpty()) {
            EmptyState empty = new EmptyState(
                    emptyStatisticsMessage(),
                    emptyStatisticsSubtitle());
            statisticsContent.getChildren().add(empty);
            StackPane.setAlignment(empty, Pos.CENTER);
        } else {
            ScrollPane cardsScroll = new ScrollPane(analysisCards);
            cardsScroll.getStyleClass().add("shared-scroll");
            cardsScroll.setFitToWidth(true);
            cardsScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            cardsScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            statisticsContent.getChildren().add(cardsScroll);
        }

        VBox statistics = new VBox(18, analysisFilter, searchInput, statisticsContent);
        VBox.setVgrow(statisticsContent, Priority.ALWAYS);

        contentArea.getChildren().setAll(statistics);
    }

    private String emptyStatisticsMessage() {
        LocalizationService i18n = LocalizationService.getInstance();
        if (!searchInput.getText().isBlank()) {
            return i18n.get("nothingFound");
        }

        if (analysisFilter.getSelectedIndex() == 1) {
            return i18n.get("archive.noSavedTermAnalyses");
        }
        if (analysisFilter.getSelectedIndex() == 2) {
            return i18n.get("archive.noSavedTextComparisons");
        }
        if (analysisFilter.getSelectedIndex() == 3) {
            return i18n.get("archive.noSavedTermComparisons");
        }
        return i18n.get("archive.noSavedTextAnalyses");
    }

    private String emptyStatisticsSubtitle() {
        LocalizationService i18n = LocalizationService.getInstance();
        if (!searchInput.getText().isBlank()) {
            return i18n.get("tryChangingSearchQuery");
        }

        if (analysisFilter.getSelectedIndex() >= 2) {
            return i18n.get("archive.runAndSaveComparison");
        }
        return i18n.get("archive.runAndSaveAnalysis");
    }

    private boolean matchesSearch(String... values) {
        String searchText = searchInput.getText().trim().toLowerCase();

        if (searchText.isEmpty()) {
            return true;
        }

        for (String value : values) {
            if (value != null && value.toLowerCase().contains(searchText)) {
                return true;
            }
        }

        return false;
    }

}
