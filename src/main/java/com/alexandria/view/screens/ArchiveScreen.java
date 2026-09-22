package com.alexandria.view.screens;

import com.alexandria.model.ArchiveTermAnalysis;
import com.alexandria.model.ArchiveTextAnalysis;
import com.alexandria.service.analysis.TermAnalysisResult;
import com.alexandria.service.analysis.TextAnalysisResult;
import com.alexandria.service.analysis.TextFragment;
import com.alexandria.service.analysis.WordFrequency;
import com.alexandria.view.components.shared.modal.Modal;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;

public class ArchiveScreen extends StackPane {

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("d MMM uuuu");

    private final ToggleButton statisticsButton = new ToggleButton("Statistics");
    private final ToggleButton quotationsButton = new ToggleButton("Quotations");
    private final ToggleButton textFilterButton = new ToggleButton("Text analysis");
    private final ToggleButton termFilterButton = new ToggleButton("Term analysis");
    private final FlowPane cardsHost = new FlowPane(16, 16);
    private final StackPane contentHost = new StackPane();
    private final BorderPane loadedState = new BorderPane();
    private final VBox body = buildBody();
    private final Modal modal = new Modal();

    private List<ArchiveTextAnalysis> textAnalyses = List.of();
    private List<ArchiveTermAnalysis> termAnalyses = List.of();

    private Consumer<Integer> onDeleteTextAnalysis = id -> {};
    private Consumer<Integer> onDeleteTermAnalysis = id -> {};
    private Runnable onShown = () -> {};
    private Runnable onSignIn = () -> {};

    public ArchiveScreen() {
        getStyleClass().add("archive-screen");

        loadedState.setTop(buildHeader());
        loadedState.setCenter(body);
        loadedState.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        getChildren().addAll(loadedState, modal);
        StackPane.setAlignment(loadedState, Pos.CENTER);
        StackPane.setAlignment(modal, Pos.CENTER);
        modal.prefWidthProperty().bind(widthProperty());
        modal.prefHeightProperty().bind(heightProperty());

        wireCallbacks();
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

        VBox body = new VBox(18, sectionSwitcher, contentHost);
        body.getStyleClass().add("archive-body");
        VBox.setVgrow(contentHost, Priority.ALWAYS);
        return body;
    }

    private void wireCallbacks() {
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

        loadedState.setCenter(signInContent);
        BorderPane.setAlignment(signInContent, Pos.CENTER);
    }

    private void showStatistics() {
        if (!statisticsButton.isSelected()) {
            return;
        }

        loadedState.setCenter(body);

        cardsHost.getChildren().clear();

        if (textFilterButton.isSelected()) {
            for (ArchiveTextAnalysis analysis : textAnalyses) {
                cardsHost.getChildren().add(createTextAnalysisCard(analysis));
            }
        }

        if (termFilterButton.isSelected()) {
            for (ArchiveTermAnalysis analysis : termAnalyses) {
                cardsHost.getChildren().add(createTermAnalysisCard(analysis));
            }
        }

        StackPane statisticsContent = new StackPane();
        statisticsContent.getStyleClass().add("archive-content");

        if (cardsHost.getChildren().isEmpty()) {
            Label empty = new Label(emptyStatisticsMessage());
            empty.getStyleClass().add("archive-empty");
            statisticsContent.getChildren().add(empty);
            StackPane.setAlignment(empty, Pos.CENTER);
        } else {
            ScrollPane cardsScroll = new ScrollPane(cardsHost);
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

        contentHost.getChildren().setAll(statistics);
    }

    private void showQuotations() {
        if (!quotationsButton.isSelected()) {
            return;
        }

        loadedState.setCenter(body);
        showEmpty("No saved quotations yet");
    }

    private VBox createTextAnalysisCard(ArchiveTextAnalysis analysis) {
        String titleText = analysis.getProjectTitle();
        if (titleText == null || titleText.isBlank()) {
            titleText = analysis.getSourceFileName();
        }

        Label title = new Label(titleText);
        title.getStyleClass().add("heading-md");
        title.setWrapText(true);

        Label source = new Label(analysis.getSourceFileName());
        source.getStyleClass().add("text-muted");
        source.setWrapText(true);
        VBox textContent = new VBox(4, title, source);

        return createArchiveCard("Text analysis", textContent, analysis.getCreatedAt(), analysis.getId(),
                onDeleteTextAnalysis, () -> showTextAnalysisModal(analysis));
    }

    private VBox createTermAnalysisCard(ArchiveTermAnalysis analysis) {
        String titleText = analysis.getProjectTitle();
        if (titleText == null || titleText.isBlank()) {
            titleText = analysis.getSourceFileName();
        }

        Label title = new Label(titleText);
        title.getStyleClass().add("heading-md");
        title.setWrapText(true);

        Label term = new Label("Term: " + analysis.getTerm());
        term.getStyleClass().add("archive-term-preview");
        term.setWrapText(true);

        Label source = new Label(analysis.getSourceFileName());
        source.getStyleClass().add("text-muted");
        source.setWrapText(true);
        VBox textContent = new VBox(4, title, term, source);

        return createArchiveCard("Term analysis", textContent, analysis.getCreatedAt(), analysis.getId(),
                onDeleteTermAnalysis, () -> showTermAnalysisModal(analysis));
    }

    private VBox createArchiveCard(String typeText, VBox textContent, LocalDateTime createdAt,
            Integer id, Consumer<Integer> deleteHandler, Runnable openHandler) {
        VBox card = new VBox(12);
        card.getStyleClass().add("card");
        card.setPrefWidth(280);
        card.setPrefHeight(200);

        Label type = new Label(typeText);
        type.getStyleClass().add("tags");

        Button delete = createDeleteButton(id, deleteHandler);
        AnchorPane topRow = new AnchorPane(type, delete);
        topRow.setPrefHeight(20);
        AnchorPane.setTopAnchor(type, 0.0);
        AnchorPane.setLeftAnchor(type, 0.0);
        AnchorPane.setTopAnchor(delete, -10.0);
        AnchorPane.setRightAnchor(delete, 0.0);

        Label savedAt = new Label("Saved " + formatDate(createdAt));
        savedAt.getStyleClass().addAll("text-muted", "mono-text");
        Region footerSpacer = new Region();
        HBox.setHgrow(footerSpacer, Priority.ALWAYS);
        HBox footer = new HBox(footerSpacer, savedAt);
        footer.setAlignment(Pos.CENTER_LEFT);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        card.getChildren().addAll(topRow, textContent, spacer, footer,
                createOpenButton(openHandler));
        return card;
    }

    private Button createDeleteButton(Integer id, Consumer<Integer> handler) {
        FontIcon icon = new FontIcon(FontAwesomeSolid.TRASH_ALT);
        icon.setIconSize(12);

        Button delete = new Button();
        delete.setGraphic(icon);
        delete.setTooltip(new Tooltip("Delete"));
        delete.getStyleClass().addAll("button", "archive-icon-button");
        delete.setOnAction(event -> handler.accept(id));
        return delete;
    }

    private Button createOpenButton(Runnable handler) {
        Button open = new Button("Open");
        open.getStyleClass().addAll("button", "primary");
        open.setMaxWidth(Double.MAX_VALUE);
        open.setOnAction(event -> handler.run());
        return open;
    }

    private void showTextAnalysisModal(ArchiveTextAnalysis analysis) {
        TextAnalysisResult result = analysis.getTextAnalysisResult();

        Label title = new Label("Text analysis");
        title.getStyleClass().addAll("heading-lg", "archive-modal-title");
        VBox content = new VBox(20, title);
        Label generalAnalysisTitle = new Label("General analysis");
        generalAnalysisTitle.getStyleClass().addAll("heading-md", "archive-modal-section-title");

        GridPane statisticsTable = createTable("Statistic", "Value");
        addTableRow(statisticsTable, 1, "Total words", result.totalWords());
        addTableRow(statisticsTable, 2, "Unique words", result.uniqueWords());
        addTableRow(statisticsTable, 3, "Sentences", result.totalSentences());
        addTableRow(statisticsTable, 4, "Paragraphs", result.totalParagraphs());
        VBox generalAnalysis = new VBox(16, generalAnalysisTitle, statisticsTable);

        Label frequentWordsTitle = new Label("Frequent words");
        frequentWordsTitle.getStyleClass().addAll("heading-md", "archive-modal-section-title");
        VBox frequentWords = new VBox(16, frequentWordsTitle,
                createWordsTable(result.frequentWords(), false));

        Label fragmentsTitle = new Label("Important fragments");
        fragmentsTitle.getStyleClass().addAll("heading-md", "archive-modal-section-title");

        VBox fragments = new VBox(12);

        for (int index = 0; index < result.importantFragments().size(); index++) {
            TextFragment fragment = result.importantFragments().get(index);
            Label fragmentLabel = new Label((index + 1) + ". " + fragment.text());
            fragmentLabel.getStyleClass().add("archive-fragment");
            fragmentLabel.setWrapText(true);
            fragments.getChildren().add(fragmentLabel);

            if (index < result.importantFragments().size() - 1) {
                Separator divider = new Separator();
                divider.getStyleClass().add("archive-fragment-divider");
                fragments.getChildren().add(divider);
            }
        }

        VBox importantFragments = new VBox(16, fragmentsTitle, fragments);
        content.getChildren().addAll(generalAnalysis, frequentWords, importantFragments);

        showModal(content);
    }

    private void showTermAnalysisModal(ArchiveTermAnalysis analysis) {
        TermAnalysisResult result = analysis.getTermAnalysisResult();

        Label title = new Label("Term analysis");
        title.getStyleClass().addAll("heading-lg", "archive-modal-title");
        Label analyzedTermLabel = new Label("Analyzed term");
        analyzedTermLabel.getStyleClass().add("text-muted");
        Label term = new Label(result.term());
        term.getStyleClass().add("archive-modal-term");
        VBox analyzedTerm = new VBox(2, analyzedTermLabel, term);
        VBox termHeader = new VBox(16, title, analyzedTerm);
        VBox content = new VBox(20, termHeader);

        Label generalAnalysisTitle = new Label("General analysis");
        generalAnalysisTitle.getStyleClass().addAll("heading-md", "archive-modal-section-title");

        GridPane statisticsTable = createTable("Statistic", "Value");
        addTableRow(statisticsTable, 1, "Occurrences", result.totalOccurrences());
        addTableRow(statisticsTable, 2, "Per 1,000 words", result.relativeFrequency());
        addTableRow(statisticsTable, 3, "Sentences", result.sentenceCount());
        addTableRow(statisticsTable, 4, "Paragraphs", result.paragraphCount());
        VBox generalAnalysis = new VBox(16, generalAnalysisTitle, statisticsTable);

        Label neighboringWordsTitle = new Label("Neighboring words");
        neighboringWordsTitle.getStyleClass().addAll("heading-md", "archive-modal-section-title");
        VBox neighboringWords = new VBox(16, neighboringWordsTitle,
                createWordsTable(result.neighboringWords(), true));

        content.getChildren().addAll(generalAnalysis, neighboringWords);
        showModal(content);
    }

    private GridPane createTable(String firstHeader, String secondHeader) {
        GridPane table = new GridPane();
        table.getStyleClass().add("archive-analysis-table");

        ColumnConstraints firstColumn = new ColumnConstraints();
        firstColumn.setPercentWidth(65);
        ColumnConstraints secondColumn = new ColumnConstraints();
        secondColumn.setPercentWidth(35);
        table.getColumnConstraints().addAll(firstColumn, secondColumn);

        addTableCell(table, 0, 0, firstHeader, "archive-table-header");
        addTableCell(table, 1, 0, secondHeader, "archive-table-header");
        return table;
    }

    private void addTableRow(GridPane table, int row, String label, Object value) {
        addTableCell(table, 0, row, label, "archive-table-cell");
        addTableCell(table, 1, row, String.valueOf(value), "archive-table-cell");
    }

    private Label addTableCell(GridPane table, int column, int row, String text, String styleClass) {
        Label cell = new Label(text);
        cell.getStyleClass().add(styleClass);
        cell.setMaxWidth(Double.MAX_VALUE);
        cell.setWrapText(true);
        table.add(cell, column, row);
        return cell;
    }

    private GridPane createWordsTable(List<WordFrequency> words, boolean italicWords) {
        GridPane table = createTable("Word", "Count");
        int row = 1;

        for (WordFrequency word : words) {
            Label wordLabel = addTableCell(table, 0, row, capitalize(word.word()), "archive-table-cell");

            if (italicWords) {
                wordLabel.getStyleClass().add("archive-neighboring-word");
            }

            addTableCell(table, 1, row, String.valueOf(word.count()), "archive-table-cell");
            row++;
        }

        return table;
    }

    private String capitalize(String word) {
        if (word == null || word.isEmpty()) {
            return word;
        }

        return word.substring(0, 1).toUpperCase() + word.substring(1);
    }

    private void showModal(VBox content) {
        ScrollPane scroll = new ScrollPane(content);
        scroll.getStyleClass().add("archive-modal-scroll");
        scroll.setFitToWidth(true);
        scroll.setPrefViewportHeight(520);
        scroll.setMaxWidth(Double.MAX_VALUE);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        VBox modalContent = new VBox(scroll);
        modalContent.getStyleClass().addAll("modal-card", "archive-analysis-modal");
        modalContent.setPadding(new Insets(24));
        modalContent.setPrefWidth(400);
        modalContent.setMaxWidth(400);
        modal.show(modalContent);
    }

    private void showEmpty(String message) {
        Label empty = new Label(message);
        empty.getStyleClass().add("archive-empty");

        contentHost.getChildren().setAll(empty);
        StackPane.setAlignment(empty, Pos.CENTER);
    }

    private String emptyStatisticsMessage() {
        if (termFilterButton.isSelected()) {
            return "No saved term statistics yet";
        }
        return "No saved text statistics yet";
    }

    private String formatDate(LocalDateTime date) {
        return date == null ? "unknown date" : DATE_FORMAT.format(date);
    }
}
