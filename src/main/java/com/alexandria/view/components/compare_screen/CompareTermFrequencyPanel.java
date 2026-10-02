package com.alexandria.view.components.compare_screen;

import com.alexandria.service.analysis.WordFrequency;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;

public class CompareTermFrequencyPanel extends VBox {

    private static final int TOP_WORD_COUNT = 5;
    private static final double FILLED_PORTION = 1;

    private final VBox rowsHost = new VBox(10);

    private final Label emptyLabel =
            new Label("Open two documents to compare frequent words.");

    private final Label docALegendLabel =
            new Label("Doc A");

    private final Label docBLegendLabel =
            new Label("Doc B");

    private Consumer<String> onRowClick = word -> {};

    public CompareTermFrequencyPanel() {
        setSpacing(12);

        emptyLabel.getStyleClass().add("text-muted");

        Label heading =
                new Label("Term Frequency (Top 5)");
        heading.getStyleClass().add("heading-sm");

        Label ratioTitle =
                new Label("Relative Ratio");
        ratioTitle.getStyleClass().add("text-muted");

        HBox headingRow = new HBox();
        headingRow.setAlignment(Pos.CENTER_LEFT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        headingRow.getChildren().addAll(
                heading,
                spacer,
                ratioTitle
        );

        getChildren().addAll(
                headingRow,
                rowsHost,
                buildLegend()
        );

        showEmpty();
    }

    public void setResults(
            List<WordFrequency> docA,
            List<WordFrequency> docB) {

        rowsHost.getChildren().clear();

        if (docA == null
                || docB == null
                || docA.isEmpty()
                || docB.isEmpty()) {

            showEmpty();
            return;
        }

        Map<String, WordFrequency> frequenciesA =
                toFrequencyMap(docA);

        Map<String, WordFrequency> frequenciesB =
                toFrequencyMap(docB);

        List<ComparisonWord> commonWords =
                new ArrayList<>();

        for (Map.Entry<String, WordFrequency> entry
                : frequenciesA.entrySet()) {

            WordFrequency a = entry.getValue();
            WordFrequency b = frequenciesB.get(entry.getKey());

            if (b != null) {
                commonWords.add(
                        new ComparisonWord(
                                displayWord(a, b),
                                a.count(),
                                b.count()
                        )
                );
            }
        }

        commonWords.sort(
                Comparator
                        .comparingInt(
                                ComparisonWord::totalCount)
                        .reversed()
                        .thenComparing(
                                ComparisonWord::word,
                                String.CASE_INSENSITIVE_ORDER)
        );

        setComparisonResults(commonWords);
    }

    public void setComparisonResults(
            List<ComparisonWord> words) {

        rowsHost.getChildren().clear();

        if (words == null || words.isEmpty()) {
            showEmpty();
            return;
        }

        int limit = Math.min(
                TOP_WORD_COUNT,
                words.size()
        );

        for (int i = 0; i < limit; i++) {
            rowsHost.getChildren().add(
                    buildRow(words.get(i))
            );
        }
    }

    public void setDocumentNames(
            String docAFileName,
            String docBFileName) {

        docALegendLabel.setText(
                formatDocumentName(
                        "Doc A",
                        docAFileName
                )
        );

        docBLegendLabel.setText(
                formatDocumentName(
                        "Doc B",
                        docBFileName
                )
        );
    }

    private String formatDocumentName(
            String documentLabel,
            String fileName) {

        if (fileName == null || fileName.isBlank()) {
            return documentLabel;
        }

        return documentLabel + ": " + fileName;
    }

    private Map<String, WordFrequency> toFrequencyMap(
            List<WordFrequency> words) {

        Map<String, WordFrequency> frequencies =
                new LinkedHashMap<>();

        for (WordFrequency frequency : words) {

            if (frequency == null
                    || frequency.word() == null) {
                continue;
            }

            String key =
                    frequency.word()
                            .trim()
                            .toLowerCase(Locale.ROOT);

            if (key.isEmpty()) {
                continue;
            }

            WordFrequency existing =
                    frequencies.get(key);

            if (existing == null
                    || frequency.count() > existing.count()) {

                frequencies.put(
                        key,
                        frequency
                );
            }
        }

        return frequencies;
    }

    private String displayWord(
            WordFrequency docAWord,
            WordFrequency docBWord) {

        String word =
                docAWord.word() == null
                        ? ""
                        : docAWord.word().trim();

        return word.isEmpty()
                ? docBWord.word()
                : word;
    }

    private HBox buildRow(
            ComparisonWord word) {

        Label termLabel =
                new Label(word.word());

        termLabel.setMinWidth(80);
        termLabel.setPrefWidth(80);

        Region docABar =
                new Region();
        docABar.getStyleClass().add(
                "doc-a-bar");

        Region docBBar =
                new Region();
        docBBar.getStyleClass().add(
                "doc-b-bar");

        StackPane frequencyBar =
                new StackPane(
                        docABar,
                        docBBar
                );

        frequencyBar.getStyleClass().add(
                "frequency-bar");

        frequencyBar.setMinWidth(0);
        frequencyBar.setPrefHeight(7);
        frequencyBar.setMaxHeight(7);

        StackPane.setAlignment(
                docABar,
                Pos.CENTER_LEFT);

        StackPane.setAlignment(
                docBBar,
                Pos.CENTER_LEFT);

        double total =
                (double) word.docACount()
                        + word.docBCount();

        double docARatio =
                total <= 0
                        ? 0
                        : word.docACount() / total;

        double docBRatio =
                total <= 0
                        ? 0
                        : word.docBCount() / total;

        docABar.prefWidthProperty().bind(
                frequencyBar.widthProperty()
                        .multiply(
                                FILLED_PORTION
                                        * docARatio
                        )
        );

        docABar.maxWidthProperty().bind(
                docABar.prefWidthProperty()
        );

        docBBar.prefWidthProperty().bind(
                frequencyBar.widthProperty()
                        .multiply(
                                FILLED_PORTION
                                        * docBRatio
                        )
        );

        docBBar.maxWidthProperty().bind(
                docBBar.prefWidthProperty()
        );

        docBBar.translateXProperty().bind(
                docABar.prefWidthProperty()
        );

        HBox counts =
                buildCounts(
                        word.docACount(),
                        word.docBCount()
                );

        HBox.setHgrow(
                frequencyBar,
                Priority.ALWAYS
        );

        HBox row =
                new HBox(
                        10,
                        termLabel,
                        frequencyBar,
                        counts
                );

        row.setAlignment(
                Pos.CENTER_LEFT
        );

        row.getStyleClass().add(
                "frequency-row"
        );

        row.setOnMouseClicked(
                event ->
                        onRowClick.accept(
                                word.word()
                        )
        );

        return row;
    }

    private HBox buildCounts(
            int docACount,
            int docBCount) {

        Label docATitle =
                new Label("Doc A:");

        Label docAValue =
                new Label(
                        String.valueOf(docACount)
                );

        Label separator =
                new Label("|");

        Label docBTitle =
                new Label("Doc B:");

        Label docBValue =
                new Label(
                        String.valueOf(docBCount)
                );

        docATitle.getStyleClass().add(
                "text-muted"
        );

        docAValue.getStyleClass().add(
                "text-muted"
        );

        separator.getStyleClass().add(
                "text-muted"
        );

        docBTitle.getStyleClass().add(
                "text-muted"
        );

        docBValue.getStyleClass().add(
                "text-muted"
        );

        docATitle.setMinWidth(34);
        docATitle.setPrefWidth(34);

        docAValue.setMinWidth(24);
        docAValue.setPrefWidth(24);
        docAValue.setAlignment(
                Pos.CENTER_RIGHT
        );

        separator.setMinWidth(10);
        separator.setPrefWidth(10);
        separator.setAlignment(
                Pos.CENTER
        );

        docBTitle.setMinWidth(34);
        docBTitle.setPrefWidth(34);

        docBValue.setMinWidth(24);
        docBValue.setPrefWidth(24);
        docBValue.setAlignment(
                Pos.CENTER_RIGHT
        );

        HBox counts =
                new HBox(
                        2,
                        docATitle,
                        docAValue,
                        separator,
                        docBTitle,
                        docBValue
                );

        counts.setAlignment(
                Pos.CENTER_RIGHT
        );

        counts.setMinWidth(
                Region.USE_PREF_SIZE
        );

        return counts;
    }

    private HBox buildLegend() {

        Region docASwatch =
                new Region();

        docASwatch.getStyleClass().add(
                "doc-a-swatch"
        );

        docASwatch.setPrefSize(14, 14);
        docASwatch.setMinSize(14, 14);

        docALegendLabel.getStyleClass().add(
                "text-muted"
        );

        Region docBSwatch =
                new Region();

        docBSwatch.getStyleClass().add(
                "doc-b-swatch"
        );

        docBSwatch.setPrefSize(14, 14);
        docBSwatch.setMinSize(14, 14);

        docBLegendLabel.getStyleClass().add(
                "text-muted"
        );

        HBox legend =
                new HBox(
                        8,
                        docASwatch,
                        docALegendLabel,
                        docBSwatch,
                        docBLegendLabel
                );

        legend.setAlignment(
                Pos.CENTER_RIGHT
        );

        return legend;
    }

    public void setOnRowClick(
            Consumer<String> handler) {

        onRowClick =
                handler == null
                        ? word -> {}
                        : handler;
    }

    private void showEmpty() {
        rowsHost.getChildren().setAll(
                emptyLabel
        );
    }

    public record ComparisonWord(
            String word,
            int docACount,
            int docBCount) {

        public int totalCount() {
            return docACount + docBCount;
        }
    }
}