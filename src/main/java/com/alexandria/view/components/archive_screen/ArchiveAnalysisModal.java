package com.alexandria.view.components.archive_screen;

import com.alexandria.model.ArchiveTermAnalysis;
import com.alexandria.model.ArchiveTextAnalysis;
import com.alexandria.service.analysis.TermAnalysisResult;
import com.alexandria.service.analysis.TermComparisonResult;
import com.alexandria.service.analysis.TextAnalysisResult;
import com.alexandria.service.analysis.TextComparisonResult;
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

    private static final int SNIPPET_CHAR_BUDGET = 180;

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

    public void showTextComparison(ArchiveComparison comparison) {
        TextComparisonResult result = comparison.textResult();

        Label title = new Label("Text comparison");
        title.getStyleClass().addAll("heading-lg", "archive-modal-title");
        Label texts = new Label(comparison.title());
        texts.getStyleClass().add("text-muted");
        texts.setWrapText(true);
        VBox content = new VBox(20, new VBox(8, title, texts));

        Label summaryTitle = new Label("Similarity");
        summaryTitle.getStyleClass().addAll("heading-md", "archive-modal-section-title");
        GridPane summaryTable = createTable("Statistic", "Value");
        double score = result.similarityScore();
        addTableRow(summaryTable, 1, "Similarity index", Math.round(score) + "%");
        addTableRow(summaryTable, 2, "Correlation", result.similarityAmount());
        VBox summary = new VBox(16, summaryTitle, summaryTable);

        Label wordsTitle = new Label("Most frequent shared words");
        wordsTitle.getStyleClass().addAll("heading-md", "archive-modal-section-title");
        GridPane wordsTable = createTable("Word", "Doc A · Doc B");
        int row = 1;
        for (TextComparisonResult.TextComparisonRow word : result.commonWords()) {
            addTableCell(wordsTable, 0, row, capitalize(word.word()), "archive-table-cell");
            addTableCell(wordsTable, 1, row, countsForDocuments(word, result.textIds()), "archive-table-cell");
            row++;
        }
        VBox sharedWords = new VBox(16, wordsTitle, wordsTable);

        Label paragraphsTitle = new Label("Key paragraphs");
        paragraphsTitle.getStyleClass().addAll("heading-md", "archive-modal-section-title");
        VBox paragraphs = new VBox(12);
        List<TextComparisonResult.ParagraphMatch> matches = result.similarParagraphs();
        for (int index = 0; index < matches.size(); index++) {
            TextComparisonResult.ParagraphMatch match = matches.get(index);
            Label score1 = new Label(String.format("Similarity score: %.0f%%", match.scorePercent()));
            score1.getStyleClass().add("heading-sm");
            paragraphs.getChildren().addAll(
                    score1,
                    snippetLabel("Doc A", match.first()),
                    snippetLabel("Doc B", match.second()));

            if (index < matches.size() - 1) {
                Separator divider = new Separator();
                divider.getStyleClass().add("archive-fragment-divider");
                paragraphs.getChildren().add(divider);
            }
        }
        VBox keyParagraphs = new VBox(16, paragraphsTitle, paragraphs);

        content.getChildren().addAll(summary, sharedWords, keyParagraphs);
        showAnalysis(content);
    }

    public void showTermComparison(ArchiveComparison comparison) {
        TermComparisonResult result = comparison.termResult();

        Label title = new Label("Term comparison");
        title.getStyleClass().addAll("heading-lg", "archive-modal-title");
        Label comparedTermLabel = new Label("Compared term");
        comparedTermLabel.getStyleClass().add("text-muted");
        Label term = new Label(comparison.term());
        term.getStyleClass().add("archive-modal-term");
        VBox comparedTerm = new VBox(2, comparedTermLabel, term);
        VBox content = new VBox(20, new VBox(16, title, comparedTerm));

        Label occurrencesTitle = new Label("Occurrences per text");
        occurrencesTitle.getStyleClass().addAll("heading-md", "archive-modal-section-title");
        GridPane table = createTable("Text", "Occurrences");
        int row = 1;
        for (TermComparisonResult.TermTextOccurrence occurrence : result.occurrencesPerText()) {
            addTableCell(table, 0, row, occurrence.textTitle(), "archive-table-cell");
            addTableCell(table, 1, row,
                    occurrence.occurrences() + " · "
                            + String.format("%.2f / 1,000 words", occurrence.relativeFrequency()),
                    "archive-table-cell");
            row++;
        }

        content.getChildren().add(new VBox(16, occurrencesTitle, table));
        showAnalysis(content);
    }

    private Label snippetLabel(String documentName, TextComparisonResult.ParagraphSnippet snippet) {
        String page = snippet.page() == null ? "" : "Page " + snippet.page() + " · ";
        Label label = new Label(documentName + " · " + page + "Para " + snippet.paragraphIndex()
                + ": " + truncate(snippet.text()));
        label.getStyleClass().add("archive-fragment");
        label.setWrapText(true);
        return label;
    }

    private String countsForDocuments(TextComparisonResult.TextComparisonRow row, List<Integer> textIds) {
        if (textIds == null || textIds.isEmpty()) {
            return String.valueOf(row.countsByTextId().values().stream().mapToInt(Integer::intValue).sum());
        }
        StringBuilder counts = new StringBuilder();
        for (int index = 0; index < textIds.size(); index++) {
            if (index > 0) {
                counts.append(" · ");
            }
            counts.append(row.countsByTextId().getOrDefault(textIds.get(index), 0));
        }
        return counts.toString();
    }

    private String truncate(String text) {
        if (text == null) {
            return "";
        }
        String flat = text.replaceAll("\\s+", " ").strip();
        if (flat.length() <= SNIPPET_CHAR_BUDGET) {
            return flat;
        }
        int cut = flat.lastIndexOf(' ', SNIPPET_CHAR_BUDGET);
        return flat.substring(0, cut <= 0 ? SNIPPET_CHAR_BUDGET : cut).stripTrailing() + "...";
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
        scroll.getStyleClass().add("shared-scroll");
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