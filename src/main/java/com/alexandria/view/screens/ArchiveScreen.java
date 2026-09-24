package com.alexandria.view.screens;

import com.alexandria.model.ArchiveTermAnalysis;
import com.alexandria.model.ArchiveTextAnalysis;
import com.alexandria.view.components.archive_screen.ArchiveAnalysisCard;
import com.alexandria.view.components.archive_screen.ArchiveAnalysisModal;
import com.alexandria.view.components.shared.EmptyState;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.function.Consumer;

public class ArchiveScreen extends StackPane {

    // private final ToggleButton statisticsButton = new ToggleButton("Statistics");
    // private final ToggleButton quotationsButton = new ToggleButton("Quotations");
    private final ToggleButton textFilterButton = new ToggleButton("Text analysis");
    private final ToggleButton termFilterButton = new ToggleButton("Term analysis");
    private final ToggleButton textComparisonFilterButton = new ToggleButton("Text comparisons");
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
        // ToggleGroup sectionGroup = new ToggleGroup();
        // statisticsButton.setToggleGroup(sectionGroup);
        // statisticsButton.getStyleClass().add("archive-main-tab");
        // quotationsButton.setToggleGroup(sectionGroup);
        // quotationsButton.getStyleClass().add("archive-main-tab");
        // statisticsButton.setSelected(true);
        // statisticsButton.setMaxWidth(Double.MAX_VALUE);
        // quotationsButton.setMaxWidth(Double.MAX_VALUE);
        // HBox.setHgrow(statisticsButton, Priority.ALWAYS);
        // HBox.setHgrow(quotationsButton, Priority.ALWAYS);
        // HBox sectionSwitcher = new HBox(statisticsButton, quotationsButton);
        // sectionSwitcher.getStyleClass().add("archive-section-switcher");

        // VBox archiveBody = new VBox(18, sectionSwitcher, contentArea);
        VBox archiveBody = new VBox(contentArea);
        archiveBody.getStyleClass().add("archive-body");
        VBox.setVgrow(contentArea, Priority.ALWAYS);
        return archiveBody;
    }

    private void configureActions() {
        // statisticsButton.setOnAction(event -> showStatistics());
        // quotationsButton.setOnAction(event -> showQuotations());

        ToggleGroup filterGroup = new ToggleGroup();
        textFilterButton.setToggleGroup(filterGroup);
        textFilterButton.getStyleClass().add("pill");
        textFilterButton.setOnAction(event -> showStatistics());
        termFilterButton.setToggleGroup(filterGroup);
        termFilterButton.getStyleClass().add("pill");
        termFilterButton.setOnAction(event -> showStatistics());
        textComparisonFilterButton.setToggleGroup(filterGroup);
        textComparisonFilterButton.getStyleClass().add("pill");
        textComparisonFilterButton.setOnAction(event -> showStatistics());
        textFilterButton.setSelected(true);
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
        // if (!statisticsButton.isSelected()) {
        //     return;
        // }

        archiveLayout.setCenter(archiveBody);

        analysisCards.getChildren().clear();

        if (textFilterButton.isSelected()) {
            for (ArchiveTextAnalysis analysis : textAnalyses) {
                ArchiveAnalysisCard card = new ArchiveAnalysisCard(analysis);
                card.getDeleteButton().setOnAction(event -> onDeleteTextAnalysis.accept(analysis.getId()));
                card.getOpenButton().setOnAction(event -> analysisModal.showTextAnalysis(analysis));
                analysisCards.getChildren().add(card);
            }
        }

        if (termFilterButton.isSelected()) {
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

        HBox filterRow = new HBox(textFilterButton, termFilterButton, textComparisonFilterButton);
        filterRow.getStyleClass().add("archive-filter-row");

        VBox statistics = new VBox(18, filterRow, statisticsContent);
        VBox.setVgrow(statisticsContent, Priority.ALWAYS);

        contentArea.getChildren().setAll(statistics);
    }

    // private void showQuotations() {
    //     if (!quotationsButton.isSelected()) {
    //         return;
    //     }
    // 
    //     archiveLayout.setCenter(archiveBody);
    //     showEmpty("No saved quotations yet", "Save quotations from your text to view them here.");
    // }
    // 
    // private void showEmpty(String title, String subtitle) {
    //     EmptyState empty = new EmptyState(title, subtitle);
    // 
    //     contentArea.getChildren().setAll(empty);
    //     StackPane.setAlignment(empty, Pos.CENTER);
    // }

    private String emptyStatisticsMessage() {
        if (termFilterButton.isSelected()) {
            return "No saved term analyses yet";
        }
        if (textComparisonFilterButton.isSelected()) {
            return "No saved text comparisons yet";
        }
        return "No saved text analyses yet";
    }

    private String emptyStatisticsSubtitle() {
        if (textComparisonFilterButton.isSelected()) {
            return "Run a text comparison and save it to view it here";
        }
        return "Run an analysis and save it to view it here";
    }

}
