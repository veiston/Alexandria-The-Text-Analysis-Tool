package com.alexandria.view.components.archive_screen;

import com.alexandria.model.ArchiveComparison;
import com.alexandria.model.ArchiveTermAnalysis;
import com.alexandria.model.ArchiveTextAnalysis;
import com.alexandria.service.LocalizationService;
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
        LocalizationService i18n = LocalizationService.getInstance();
        TextAnalysisResult result = analysis.getTextAnalysisResult();

        Label title = new Label(i18n.get("textAnalysis"));
        title.getStyleClass().addAll("heading-lg", "archive-modal-title");
        VBox content = new VBox(20, title);

        Label generalAnalysisTitle = new Label(i18n.get("archive.generalAnalysis"));
        generalAnalysisTitle.getStyleClass().addAll("heading-md", "archive-modal-section-title");
        GridPane statisticsTable = createTable(i18n.get("archive.statistic"), i18n.get("archive.value"));
        addTableRow(statisticsTable, 1, i18n.get("archive.totalWords"), result.totalWords());
        addTableRow(statisticsTable, 2, i18n.get("archive.uniqueWords"), result.uniqueWords());
        addTableRow(statisticsTable, 3, i18n.get("archive.sentences"), result.totalSentences());
        addTableRow(statisticsTable, 4, i18n.get("archive.paragraphs"), result.totalParagraphs());
        VBox generalAnalysis = new VBox(16, generalAnalysisTitle, statisticsTable);

        Label frequentWordsTitle = new Label(i18n.get("archive.frequentWords"));
        frequentWordsTitle.getStyleClass().addAll("heading-md", "archive-modal-section-title");
        VBox frequentWords = new VBox(16, frequentWordsTitle,
                createWordsTable(result.frequentWords(), false));

        Label fragmentsTitle = new Label(i18n.get("archive.importantFragments"));
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
        LocalizationService i18n = LocalizationService.getInstance();
        TermAnalysisResult result = analysis.getTermAnalysisResult();

        Label title = new Label(i18n.get("termAnalysis"));
        title.getStyleClass().addAll("heading-lg", "archive-modal-title");
        Label analyzedTermLabel = new Label(i18n.get("archive.analyzedTerm"));
        analyzedTermLabel.getStyleClass().add("text-muted");
        Label term = new Label(result.term());
        term.getStyleClass().add("archive-modal-term");
        VBox analyzedTerm = new VBox(2, analyzedTermLabel, term);
        VBox termHeader = new VBox(16, title, analyzedTerm);
        VBox content = new VBox(20, termHeader);

        Label generalAnalysisTitle = new Label(i18n.get("archive.generalAnalysis"));
        generalAnalysisTitle.getStyleClass().addAll("heading-md", "archive-modal-section-title");
        GridPane statisticsTable = createTable(i18n.get("archive.statistic"), i18n.get("archive.value"));
        addTableRow(statisticsTable, 1, i18n.get("archive.occurrences"), result.totalOccurrences());
        addTableRow(statisticsTable, 2, i18n.get("archive.perThousandWords"), result.relativeFrequency());
        addTableRow(statisticsTable, 3, i18n.get("archive.sentences"), result.sentenceCount());
        addTableRow(statisticsTable, 4, i18n.get("archive.paragraphs"), result.paragraphCount());
        VBox generalAnalysis = new VBox(16, generalAnalysisTitle, statisticsTable);

        Label neighboringWordsTitle = new Label(i18n.get("archive.neighboringWords"));
        neighboringWordsTitle.getStyleClass().addAll("heading-md", "archive-modal-section-title");
        VBox neighboringWords = new VBox(16, neighboringWordsTitle,
                createWordsTable(result.neighboringWords(), true));

        content.getChildren().addAll(generalAnalysis, neighboringWords);
        showAnalysis(content);
    }

    public void showTextComparison(ArchiveComparison comparison) {
        LocalizationService i18n = LocalizationService.getInstance();
        TextComparisonResult result = comparison.textResult();

        Label title = new Label(i18n.get("textComparison"));
        title.getStyleClass().addAll("heading-lg", "archive-modal-title");
        Label texts = new Label(comparison.title());
        texts.getStyleClass().add("text-muted");
        texts.setWrapText(true);
        VBox content = new VBox(20, new VBox(8, title, texts));

        Label summaryTitle = new Label(i18n.get("archive.similarity"));
        summaryTitle.getStyleClass().addAll("heading-md", "archive-modal-section-title");
        GridPane summaryTable = createTable(i18n.get("archive.statistic"), i18n.get("archive.value"));
        double score = result.similarityScore();
        addTableRow(summaryTable, 1, i18n.get("archive.similarityIndex"), Math.round(score) + "%");
        addTableRow(summaryTable, 2, i18n.get("archive.correlation"), result.similarityAmount());
        VBox summary = new VBox(16, summaryTitle, summaryTable);

        Label wordsTitle = new Label(i18n.get("archive.mostFrequentSharedWords"));
        wordsTitle.getStyleClass().addAll("heading-md", "archive-modal-section-title");
        GridPane wordsTable = createTable(i18n.get("archive.word"), i18n.get("archive.documentsHeader"));
        int row = 1;
        for (TextComparisonResult.TextComparisonRow word : result.commonWords()) {
            addTableCell(wordsTable, 0, row, capitalize(word.word()), "archive-table-cell");
            addTableCell(wordsTable, 1, row, countsForDocuments(word, result.textIds()), "archive-table-cell");
            row++;
        }
        VBox sharedWords = new VBox(16, wordsTitle, wordsTable);

        Label paragraphsTitle = new Label(i18n.get("archive.keyParagraphs"));
        paragraphsTitle.getStyleClass().addAll("heading-md", "archive-modal-section-title");
        VBox paragraphs = new VBox(12);
        List<TextComparisonResult.ParagraphMatch> matches = result.similarParagraphs();
        for (int index = 0; index < matches.size(); index++) {
            TextComparisonResult.ParagraphMatch match = matches.get(index);
            Label score1 = new Label(i18n.get("archive.similarityScore").replace("{score}", String.format("%.0f", match.scorePercent())));
            score1.getStyleClass().add("heading-sm");
            paragraphs.getChildren().addAll(
                    score1,
                    snippetLabel(i18n.get("archive.documentA"), match.first()),
                    snippetLabel(i18n.get("archive.documentB"), match.second()));

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
        LocalizationService i18n = LocalizationService.getInstance();
        TermComparisonResult result = comparison.termResult();

        Label title = new Label(i18n.get("termComparison"));
        title.getStyleClass().addAll("heading-lg", "archive-modal-title");
        Label comparedTermLabel = new Label(i18n.get("archive.comparedTerm"));
        comparedTermLabel.getStyleClass().add("text-muted");
        Label term = new Label(comparison.term());
        term.getStyleClass().add("archive-modal-term");
        VBox comparedTerm = new VBox(2, comparedTermLabel, term);
        VBox content = new VBox(20, new VBox(16, title, comparedTerm));

        Label occurrencesTitle = new Label(i18n.get("archive.occurrencesPerText"));
        occurrencesTitle.getStyleClass().addAll("heading-md", "archive-modal-section-title");
        GridPane table = createTable(i18n.get("archive.text"), i18n.get("archive.occurrences"));
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
        LocalizationService i18n = LocalizationService.getInstance();
        String page = snippet.page() == null ? "" : i18n.get("archive.page").replace("{page}", String.valueOf(snippet.page())) + " · ";
        Label label = new Label(documentName + " · " + page + i18n.get("archive.paragraph").replace("{paragraph}", String.valueOf(snippet.paragraphIndex()))
                + " " + truncate(snippet.text()));
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
        String formattedValue = (value instanceof Number number)
                ? com.alexandria.service.LocalizationService.getInstance().formatNumber(number.doubleValue())
                : String.valueOf(value);
        addTableCell(table, 1, row, formattedValue, "archive-table-cell");
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
        LocalizationService i18n = LocalizationService.getInstance();
        GridPane table = createTable(i18n.get("archive.word"), i18n.get("archive.count"));
        int row = 1;

        for (WordFrequency word : words) {
            Label wordLabel = addTableCell(table, 0, row, capitalize(word.word()), "archive-table-cell");

            if (italicWords) {
                wordLabel.getStyleClass().add("archive-neighboring-word");
            }

            addTableCell(table, 1, row, com.alexandria.service.LocalizationService.getInstance().formatNumber(word.count()), "archive-table-cell");
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
        com.alexandria.service.LocalizationService.getInstance().apply(content);
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
