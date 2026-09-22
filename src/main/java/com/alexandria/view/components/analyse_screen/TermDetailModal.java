package com.alexandria.view.components.analyse_screen;

import com.alexandria.service.analysis.SearchMatch;
import com.alexandria.service.analysis.TermAnalysisResult;
import com.alexandria.service.analysis.WordFrequency;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.function.IntConsumer;

/**
 * Shows term statistics, nearby words, and located occurrences with a
 * "Go to →" button per occurrence. The go-to handler is supplied by the
 * caller (see setData) so this modal has no dependency on how navigation
 * is actually implemented — in PR1 it's a no-op placeholder; PR3 wires it
 * to DocumentView.jumpToMatch once real document navigation exists.
 */
public class TermDetailModal extends VBox {

    private final Label titleLabel = new Label();
    private final Label statsLabel = new Label();
    private final VBox neighborsHost = new VBox(6);
    private final VBox occurrencesHost = new VBox(8);

    public TermDetailModal() {
        getStyleClass().add("modal-card");
        setSpacing(16);
        setPadding(new Insets(24));
        setPrefWidth(460);
        setMaxHeight(560);

        titleLabel.getStyleClass().add("heading-lg");
        statsLabel.getStyleClass().add("text-muted");

        Label neighborsHeading = new Label("Nearby Words");
        neighborsHeading.getStyleClass().add("heading-sm");
        Label occurrencesHeading = new Label("Occurrences");
        occurrencesHeading.getStyleClass().add("heading-sm");

        ScrollPane occurrencesScroll = new ScrollPane(occurrencesHost);
        occurrencesScroll.setFitToWidth(true);
        occurrencesScroll.getStyleClass().add("inputed-scroll");

        getChildren().addAll(
                titleLabel, statsLabel,
                neighborsHeading, neighborsHost,
                occurrencesHeading, occurrencesScroll
        );
    }

    public void setData(
            String term,
            TermAnalysisResult analysis,
            List<SearchMatch> matches,
            IntConsumer onJumpToMatch
    ) {
        titleLabel.setText("\"" + term + "\"");
        statsLabel.setText(String.format(
                "%d occurrences · %.1f per 1,000 words · %d sentences · %d paragraphs",
                analysis.totalOccurrences(), analysis.relativeFrequency(),
                analysis.sentenceCount(), analysis.paragraphCount()
        ));

        neighborsHost.getChildren().clear();
        if (analysis.neighboringWords().isEmpty()) {
            neighborsHost.getChildren().add(mutedLabel("No nearby words found."));
        } else {
            HBox chips = new HBox(6);
            for (WordFrequency word : analysis.neighboringWords()) {
                Label chip = new Label(word.word() + " (" + word.count() + ")");
                chip.getStyleClass().add("chip");
                chips.getChildren().add(chip);
            }
            neighborsHost.getChildren().add(chips);
        }

        occurrencesHost.getChildren().clear();
        if (matches == null || matches.isEmpty()) {
            occurrencesHost.getChildren().add(mutedLabel("No located occurrences."));
        } else {
            for (int i = 0; i < matches.size(); i++) {
                occurrencesHost.getChildren().add(buildOccurrenceRow(i, matches.get(i), onJumpToMatch));
            }
        }
    }

    private HBox buildOccurrenceRow(int index, SearchMatch match, IntConsumer onJumpToMatch) {
        VBox textBox = new VBox(2);
        Label pageLabel = new Label(formatPageLabel(match.page(), match.paragraph()));
        pageLabel.getStyleClass().add("context-match-page");
        Label snippet = new Label(match.context());
        snippet.setWrapText(true);
        snippet.getStyleClass().add("context-match-snippet");
        textBox.getChildren().addAll(pageLabel, snippet);

        Button goToButton = new Button("Go to →");
        goToButton.getStyleClass().addAll("button", "secondary");
        goToButton.setOnAction(e -> onJumpToMatch.accept(index));

        HBox row = new HBox(10, textBox, goToButton);
        row.setAlignment(Pos.CENTER_LEFT);
        row.getStyleClass().add("context-match-card");
        return row;
    }

    private Label mutedLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("text-muted");
        return label;
    }

    private static String formatPageLabel(Integer page, Integer paragraph) {
        if (page != null && paragraph != null) return "Page " + page + " · Para " + paragraph;
        if (page != null) return "Page " + page;
        if (paragraph != null) return "Para " + paragraph;
        return "";
    }
}