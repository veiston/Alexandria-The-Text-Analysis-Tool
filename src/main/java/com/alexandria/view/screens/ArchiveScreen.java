package com.alexandria.view.screens;

import com.alexandria.model.ArchiveTermAnalysis;
import com.alexandria.model.ArchiveTextAnalysis;
import com.alexandria.view.components.archive_screen.ArchiveAnalysisCard;
import com.alexandria.view.components.archive_screen.ArchiveAnalysisModal;
import com.alexandria.view.components.shared.EmptyState;
import com.alexandria.view.components.shared.toggle.Toggle;

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

    private final Toggle analysisFilter = new Toggle(
            "Text analysis", "Term analysis", "Text comparisons");
    private final FlowPane analysisCards = new FlowPane(16, 16);
    private final StackPane contentArea = new StackPane();
    private final BorderPane archiveLayout = new BorderPane();
    private final VBox archiveBody = buildBody();
    private final ArchiveAnalysisModal analysisModal = new ArchiveAnalysisModal();

    private List<ArchiveTextAnalysis> textAnalyses = List.of();
    private List<ArchiveTermAnalysis> termAnalyses = List.of();

    private Consumer<Integer> onDeleteTextAnalysis = id -> {};
    private Consumer<Integer> onDeleteTermAnalysis = id -> {};
    private Runnable onShown = () -> {};

    public ArchiveScreen() {
        getStyleClass().add("archive-screen");

        archiveLayout.setTop(buildHeader());
        archiveLayout.setCenter(archiveBody);
        archiveLayout.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        getChildren().addAll(archiveLayout, analysisModal);
        StackPane.setAlignment(archiveLayout, Pos.CENTER);
        StackPane.setAlignment(analysisModal, Pos.CENTER);
        analysisModal.prefWidthProperty().bind(widthProperty());
        analysisModal.prefHeightProperty().bind(heightProperty());

        configureActions();
        showStatistics();

        sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                onShown.run();
            }
        });
    }

    private VBox buildHeader() {
        Label title = new Label("Archive");
        title.getStyleClass().add("heading-xl");

        Label subtitle = new Label("Saved text, term and comparison analyses");
        subtitle.getStyleClass().add("text-muted");
        subtitle.getStyleClass().add("archive-subtitle");

        VBox header = new VBox(6, title, subtitle);
        header.getStyleClass().add("archive-header");
        return header;
    }

    private VBox buildBody() {
        VBox archiveBody = new VBox(contentArea);
        archiveBody.getStyleClass().add("archive-body");
        VBox.setVgrow(contentArea, Priority.ALWAYS);
        return archiveBody;
    }

    private void configureActions() {
        analysisFilter.setOnToggle(index -> showStatistics());
    }

    public void setTextAnalyses(List<ArchiveTextAnalysis> textAnalyses) {
        this.textAnalyses = textAnalyses == null ? List.of() : List.copyOf(textAnalyses);
        showStatistics();
    }

    public void setTermAnalyses(List<ArchiveTermAnalysis> termAnalyses) {
        this.termAnalyses = termAnalyses == null ? List.of() : List.copyOf(termAnalyses);
        showStatistics();
    }

    public void setOnDeleteTextAnalysis(Consumer<Integer> handler) {
        onDeleteTextAnalysis = handler == null ? id -> {} : handler;
    }

    public void setOnDeleteTermAnalysis(Consumer<Integer> handler) {
        onDeleteTermAnalysis = handler == null ? id -> {} : handler;
    }

    public void setOnShown(Runnable handler) {
        onShown = handler == null ? () -> {} : handler;
    }

    public void showSignInMessage() {
        EmptyState emptyState = new EmptyState(
                "Sign in to view your archive",
                "Create an account to save and view text and term analyses here");
        archiveLayout.setCenter(emptyState);
        BorderPane.setAlignment(emptyState, Pos.CENTER);
    }

    private void showStatistics() {
        archiveLayout.setCenter(archiveBody);

        analysisCards.getChildren().clear();

        if (analysisFilter.getSelectedIndex() == 0) {
            for (ArchiveTextAnalysis analysis : textAnalyses) {
                ArchiveAnalysisCard card = new ArchiveAnalysisCard(analysis);
                card.getDeleteButton().setOnAction(event -> onDeleteTextAnalysis.accept(analysis.getId()));
                card.getOpenButton().setOnAction(event -> analysisModal.showTextAnalysis(analysis));
                analysisCards.getChildren().add(card);
            }
        }

        if (analysisFilter.getSelectedIndex() == 1) {
            for (ArchiveTermAnalysis analysis : termAnalyses) {
                ArchiveAnalysisCard card = new ArchiveAnalysisCard(analysis);
                card.getDeleteButton().setOnAction(event -> onDeleteTermAnalysis.accept(analysis.getId()));
                card.getOpenButton().setOnAction(event -> analysisModal.showTermAnalysis(analysis));
                analysisCards.getChildren().add(card);
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

        VBox statistics = new VBox(18, analysisFilter, statisticsContent);
        VBox.setVgrow(statisticsContent, Priority.ALWAYS);

        contentArea.getChildren().setAll(statistics);
    }

    private String emptyStatisticsMessage() {
        if (analysisFilter.getSelectedIndex() == 1) {
            return "No saved term analyses yet";
        }
        if (analysisFilter.getSelectedIndex() == 2) {
            return "No saved text comparisons yet";
        }
        return "No saved text analyses yet";
    }

    private String emptyStatisticsSubtitle() {
        if (analysisFilter.getSelectedIndex() == 2) {
            return "Run a text comparison and save it to view it here";
        }
        return "Run an analysis and save it to view it here";
    }

}
