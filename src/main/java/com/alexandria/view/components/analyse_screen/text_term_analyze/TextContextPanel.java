package com.alexandria.view.components.analyse_screen.text_term_analyze;

import com.alexandria.service.analysis.TextFragment;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.function.BiConsumer;

public class TextContextPanel extends VBox {
    private static final int SNIPPET_CHAR_BUDGET = 180;

    private final VBox cardsHost = new VBox(10);
    private final Label emptyLabel = new Label("Open a document to see relevant paragraphs.");
    private BiConsumer<Integer, Integer> onJump = (page, paragraph) -> {};

    public TextContextPanel() {
        getStyleClass().addAll("card", "text-context-panel");
        setSpacing(12);
        setPadding(new Insets(16));

        Label heading = new Label("Key Paragraphs");
        heading.getStyleClass().add("heading-sm");
        emptyLabel.getStyleClass().add("text-muted");

        getChildren().addAll(heading, cardsHost);
        showEmpty();
    }

    public void setResults(List<TextFragment> fragments) {
        cardsHost.getChildren().clear();
        if (fragments == null || fragments.isEmpty()) { showEmpty(); return; }
        for (int i = 0; i < fragments.size(); i++) cardsHost.getChildren().add(buildCard(i + 1, fragments.get(i)));
    }

    private VBox buildCard(int rank, TextFragment fragment) {
        Label rankLabel = new Label("#" + rank + " · " + formatPageLabel(fragment.page(), fragment.paragraph()));
        rankLabel.getStyleClass().add("context-match-page");

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // TODO go-to: wired to DocumentView.goToPage
        Hyperlink jumpLink = new Hyperlink("Go to →");
        jumpLink.getStyleClass().add("hyperlink");
        jumpLink.setOnAction(e -> onJump.accept(fragment.page(), fragment.paragraph()));

        HBox topRow = new HBox(rankLabel, spacer, jumpLink);
        topRow.setAlignment(Pos.CENTER_LEFT);

        Label snippet = new Label(truncateToSnippet(fragment.text()));
        snippet.setWrapText(true);
        snippet.getStyleClass().add("context-match-snippet");

        VBox card = new VBox(4, topRow, snippet);
        card.getStyleClass().add("context-match-card");
        return card;
    }

    public void setOnJump(BiConsumer<Integer, Integer> handler) { this.onJump = handler; }
    private void showEmpty() { cardsHost.getChildren().setAll(emptyLabel); }

    private static String formatPageLabel(Integer page, Integer paragraph) {
        if (page != null && paragraph != null) return "Page " + page + " · Para " + paragraph;
        if (page != null) return "Page " + page;
        if (paragraph != null) return "Para " + paragraph;
        return "";
    }

    private static String truncateToSnippet(String text) {
        if (text == null) return "";
        if (text.length() <= SNIPPET_CHAR_BUDGET) return text;

        int cut = text.lastIndexOf(' ', SNIPPET_CHAR_BUDGET);
        if (cut <= 0) cut = SNIPPET_CHAR_BUDGET;

        return text.substring(0, cut).stripTrailing() + "…";
    }
}