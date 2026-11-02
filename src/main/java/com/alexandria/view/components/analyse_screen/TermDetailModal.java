package com.alexandria.view.components.analyse_screen;

import com.alexandria.service.LocalizationService;
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

public class TermDetailModal extends VBox {

    private final Label titleLabel = new Label();
    private final Label statsLabel = new Label();
    private final VBox neighborsHost = new VBox(8);
    private final VBox occurrencesHost = new VBox(8);
    private final Label neighborsHeading = new Label();
    private final Label occurrencesHeading = new Label();

    public TermDetailModal() {
        getStyleClass().add("modal-card");
        setSpacing(16);
        setPadding(new Insets(24));
        setPrefWidth(460);
        setMaxHeight(560);

        titleLabel.getStyleClass().add("heading-lg");
        statsLabel.getStyleClass().add("text-muted");

        neighborsHeading.getStyleClass().add("heading-md");
        occurrencesHeading.getStyleClass().add("heading-sm");

        ScrollPane occurrencesScroll = new ScrollPane(occurrencesHost);
        occurrencesScroll.setFitToWidth(true);
        occurrencesScroll.getStyleClass().add("inputed-scroll");

        getChildren().addAll(
                titleLabel, statsLabel,
                neighborsHeading, neighborsHost,
                occurrencesHeading, occurrencesScroll);

        refreshHeadings();
    }

    private void refreshHeadings() {
        LocalizationService i18n = LocalizationService.getInstance();
        neighborsHeading.setText(i18n.get("termDetail.nearbyWords"));
        occurrencesHeading.setText(i18n.get("termDetail.occurrences"));
    }

    public void setData(
            String term,
            TermAnalysisResult analysis,
            List<SearchMatch> matches,
            IntConsumer onJumpToMatch) {
        refreshHeadings();
        LocalizationService i18n = LocalizationService.getInstance();
        titleLabel.setText("\"" + term + "\"");
        statsLabel.setText(i18n.get("termDetail.statistics")
                .replace("{occurrences}", String.valueOf(analysis.totalOccurrences()))
                .replace("{frequency}", String.format("%.1f", analysis.relativeFrequency()))
                .replace("{sentences}", String.valueOf(analysis.sentenceCount()))
                .replace("{paragraphs}", String.valueOf(analysis.paragraphCount())));

        neighborsHost.getChildren().clear();
        if (analysis.neighboringWords().isEmpty()) {
            neighborsHost.getChildren().add(mutedLabel(i18n.get("termDetail.noNearbyWords")));
        } else {
            HBox chips = new HBox(6);
            for (WordFrequency word : analysis.neighboringWords()) {
                Label chip = new Label(word.word() + " (" + i18n.formatNumber(word.count()) + ")");
                chip.getStyleClass().add("chip");
                chips.getChildren().add(chip);
            }
            neighborsHost.getChildren().add(chips);
        }

        occurrencesHost.getChildren().clear();
        if (matches == null || matches.isEmpty()) {
            occurrencesHost.getChildren().add(mutedLabel(i18n.get("termDetail.noLocatedOccurrences")));
        } else {
            for (int i = 0; i < matches.size(); i++) {
                occurrencesHost.getChildren().add(buildOccurrenceRow(i, matches.get(i), onJumpToMatch));
            }
        }
    }

    private HBox buildOccurrenceRow(int index, SearchMatch match, IntConsumer onJumpToMatch) {
        LocalizationService i18n = LocalizationService.getInstance();
        VBox textBox = new VBox(2);
        textBox.setMinWidth(0);
        HBox.setHgrow(textBox, javafx.scene.layout.Priority.ALWAYS);

        Label pageLabel = new Label(
                formatPageLabel(match.page(), match.paragraph()));
        pageLabel.getStyleClass().add("context-match-page");

        Label snippet = new Label(match.context());
        snippet.setWrapText(true);
        snippet.setMaxWidth(Double.MAX_VALUE);
        snippet.getStyleClass().add("context-match-snippet");

        textBox.getChildren().addAll(pageLabel, snippet);

        Button goToButton = new Button(i18n.get("termDetail.goTo"));
        goToButton.getStyleClass().addAll("button", "secondary");
        goToButton.setMinWidth(90);
        goToButton.setMaxWidth(100);
        goToButton.setOnAction(e -> onJumpToMatch.accept(index));

        HBox row = new HBox(10, textBox, goToButton);
        row.setAlignment(Pos.TOP_LEFT);
        row.setFillHeight(true);
        row.getStyleClass().add("context-match-card");

        return row;
    }

    private Label mutedLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("text-muted");
        return label;
    }

    private static String formatPageLabel(Integer page, Integer paragraph) {
        LocalizationService i18n = LocalizationService.getInstance();
        if (page != null && paragraph != null) {
            return i18n.get("termDetail.pageAndParagraph")
                    .replace("{page}", String.valueOf(page))
                    .replace("{paragraph}", String.valueOf(paragraph));
        }
        if (page != null) {
            return i18n.get("termDetail.page").replace("{page}", String.valueOf(page));
        }
        if (paragraph != null) {
            return i18n.get("termDetail.paragraph").replace("{paragraph}", String.valueOf(paragraph));
        }
        return "";
    }
}
