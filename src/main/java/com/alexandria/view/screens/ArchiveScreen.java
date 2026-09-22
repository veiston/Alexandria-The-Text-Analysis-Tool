package com.alexandria.view.screens;

import com.alexandria.model.ArchiveTermAnalysis;
import com.alexandria.model.ArchiveTextAnalysis;
import com.alexandria.view.components.archive_screen.ArchiveAnalysisCard;
import com.alexandria.view.components.archive_screen.ArchiveAnalysisModal;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
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

    private final ToggleButton statisticsButton = new ToggleButton("Statistics");
    private final ToggleButton quotationsButton = new ToggleButton("Quotations");
    private final ToggleButton textFilterButton = new ToggleButton("Text analysis");
    private final ToggleButton termFilterButton = new ToggleButton("Term analysis");
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
    private Runnable onSignIn = () -> {};

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

        Label subtitle = new Label("Saved statistics and quotations.");
        subtitle.getStyleClass().add("text-muted");

        VBox header = new VBox(6, title, subtitle);
        header.getStyleClass().add("archive-header");
        return header;
    }

    private VBox buildBody() {
        ToggleGroup sectionGroup = new ToggleGroup();
        statisticsButton.setToggleGroup(sectionGroup);
        statisticsButton.getStyleClass().add("archive-main-tab");
        quotationsButton.setToggleGroup(sectionGroup);
        quotationsButton.getStyleClass().add("archive-main-tab");
        statisticsButton.setSelected(true);
        statisticsButton.setMaxWidth(Double.MAX_VALUE);
        quotationsButton.setMaxWidth(Double.MAX_VALUE);
		
        HBox.setHgrow(statisticsButton, Priority.ALWAYS);
        HBox.setHgrow(quotationsButton, Priority.ALWAYS);

        HBox sectionSwitcher = new HBox(statisticsButton, quotationsButton);
        sectionSwitcher.getStyleClass().add("archive-section-switcher");

        VBox archiveBody = new VBox(18, sectionSwitcher, contentArea);
        archiveBody.getStyleClass().add("archive-body");
        VBox.setVgrow(contentArea, Priority.ALWAYS);
        return archiveBody;
    }

    private void configureActions() {
        statisticsButton.setOnAction(event -> showStatistics());
        quotationsButton.setOnAction(event -> showQuotations());

        ToggleGroup filterGroup = new ToggleGroup();
        textFilterButton.setToggleGroup(filterGroup);
        textFilterButton.getStyleClass().add("pill");
        textFilterButton.setOnAction(event -> showStatistics());
        termFilterButton.setToggleGroup(filterGroup);
        termFilterButton.getStyleClass().add("pill");
        termFilterButton.setOnAction(event -> showStatistics());
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

    public void setOnSignIn(Runnable handler) {
        onSignIn = handler == null ? () -> {} : handler;
    }

    public void showSignInMessage() {
        Label message = new Label(
                "Sign in or create an account to save and view statistics and quotations here.");
        message.getStyleClass().add("archive-sign-in-message");

        Button signInButton = new Button("Sign in");
        signInButton.getStyleClass().addAll("button", "primary");
        signInButton.setOnAction(event -> onSignIn.run());

        VBox signInContent = new VBox(16, message, signInButton);
        signInContent.setAlignment(Pos.CENTER);

        archiveLayout.setCenter(signInContent);
        BorderPane.setAlignment(signInContent, Pos.CENTER);
    }

    private void showStatistics() {
        if (!statisticsButton.isSelected()) {
            return;
        }

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
            Label empty = new Label(emptyStatisticsMessage());
            empty.getStyleClass().add("archive-empty");
            statisticsContent.getChildren().add(empty);
            StackPane.setAlignment(empty, Pos.CENTER);
        } else {
            ScrollPane cardsScroll = new ScrollPane(analysisCards);
            cardsScroll.getStyleClass().add("archive-scroll");
            cardsScroll.setFitToWidth(true);
            cardsScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            cardsScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            statisticsContent.getChildren().add(cardsScroll);
        }

        HBox filterRow = new HBox(textFilterButton, termFilterButton);
        filterRow.getStyleClass().add("archive-filter-row");

        VBox statistics = new VBox(18, filterRow, statisticsContent);
        VBox.setVgrow(statisticsContent, Priority.ALWAYS);

        contentArea.getChildren().setAll(statistics);
    }

    private void showQuotations() {
        if (!quotationsButton.isSelected()) {
            return;
        }

        archiveLayout.setCenter(archiveBody);
        showEmpty("No saved quotations yet");
    }

    private void showEmpty(String message) {
        Label empty = new Label(message);
        empty.getStyleClass().add("archive-empty");

        contentArea.getChildren().setAll(empty);
        StackPane.setAlignment(empty, Pos.CENTER);
    }

    private String emptyStatisticsMessage() {
        if (termFilterButton.isSelected()) {
            return "No saved term statistics yet";
        }
        return "No saved text statistics yet";
    }

}
