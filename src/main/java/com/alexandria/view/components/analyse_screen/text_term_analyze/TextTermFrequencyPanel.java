package com.alexandria.view.components.analyse_screen.text_term_analyze;

import com.alexandria.service.analysis.WordFrequency;
import com.alexandria.view.components.shared.FrequencyBar;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.function.Consumer;

public class TextTermFrequencyPanel extends VBox {

    private final VBox rowsHost = new VBox(10);
    private final Label emptyLabel = new Label("Open a document to see frequent words.");
    private Consumer<String> onRowClick = word -> {
    };

    public TextTermFrequencyPanel() {
        getStyleClass().add("text-term-frequency-panel");
        setSpacing(12);

        Label heading = new Label("Term Frequency (Top 5)");
        heading.getStyleClass().add("heading-md");
        emptyLabel.getStyleClass().add("text-muted");

        getChildren().addAll(heading, rowsHost);
        showEmpty();
    }

    public void setResults(List<WordFrequency> words) {
        rowsHost.getChildren().clear();

        if (words == null || words.isEmpty()) {
            showEmpty();
            return;
        }

        int maxCount = words.stream().mapToInt(WordFrequency::count).max().orElse(1);
        for (WordFrequency word : words) {
            rowsHost.getChildren().add(buildRow(word, maxCount));
        }
    }

    private HBox buildRow(WordFrequency word, int maxCount) {
        Label termLabel = new Label(word.word());
        termLabel.getStyleClass().add("term-frequency-label");
        termLabel.setMinWidth(80);
        termLabel.setPrefWidth(80);

        double ratio = maxCount <= 0 ? 0 : (double) word.count() / maxCount;

        FrequencyBar frequencyBar = new FrequencyBar();
        frequencyBar.setRatio(ratio);

        HBox.setHgrow(frequencyBar, Priority.ALWAYS);

        Label countLabel = new Label(String.valueOf(word.count()));
        countLabel.getStyleClass().add("text-muted");
        countLabel.setMinWidth(36);

        HBox row = new HBox(10, termLabel, frequencyBar, countLabel);
        row.setAlignment(Pos.CENTER_LEFT);
        row.getStyleClass().add("frequency-row");
        row.setOnMouseClicked(event -> onRowClick.accept(word.word()));

        return row;
    }

    public void setOnRowClick(Consumer<String> handler) {
        onRowClick = handler == null ? word -> {
        } : handler;
    }

    private void showEmpty() {
        rowsHost.getChildren().setAll(emptyLabel);
    }
}