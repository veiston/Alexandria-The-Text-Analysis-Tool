package com.alexandria.view.components.archive_screen;

import com.alexandria.model.ArchiveTermAnalysis;
import com.alexandria.model.ArchiveTextAnalysis;
import com.alexandria.service.analysis.TermAnalysisResult;
import com.alexandria.service.analysis.TextAnalysisResult;
import com.alexandria.service.analysis.TextFragment;
import com.alexandria.service.analysis.WordFrequency;
import com.alexandria.view.components.shared.modal.Modal;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.util.List;

public class ArchiveAnalysisModal extends Modal {

    public void showTextAnalysis(ArchiveTextAnalysis analysis) {
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
        showAnalysis(content);
    }

    public void showTermAnalysis(ArchiveTermAnalysis analysis) {
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
        showAnalysis(content);
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

    private void showAnalysis(VBox content) {
        ScrollPane scroll = new ScrollPane(content);
        scroll.getStyleClass().add("archive-modal-scroll");
        scroll.setFitToWidth(true);
        scroll.setPrefViewportHeight(520);
        scroll.setMaxWidth(Double.MAX_VALUE);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        VBox analysisDialog = new VBox(scroll);
        analysisDialog.getStyleClass().addAll("modal-card", "archive-analysis-modal");
        analysisDialog.setPadding(new Insets(24));
        analysisDialog.setPrefWidth(400);
        analysisDialog.setMaxWidth(400);
        show(analysisDialog);
    }
}
