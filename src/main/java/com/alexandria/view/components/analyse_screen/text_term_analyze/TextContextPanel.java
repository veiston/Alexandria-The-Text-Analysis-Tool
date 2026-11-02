package com.alexandria.view.components.analyse_screen.text_term_analyze;

import com.alexandria.service.LocalizationService;
import com.alexandria.service.analysis.TextFragment;
import com.alexandria.view.components.shared.ContextMatchCard;
import com.alexandria.view.components.shared.PassagePreview;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.function.Consumer;

public class TextContextPanel extends VBox {
    private static final int SNIPPET_CHAR_BUDGET = 180;

    private final VBox cardsHost = new VBox(10);
    private final Label emptyLabel = new Label();
    private final Label heading = new Label();
    private List<TextFragment> currentFragments = List.of();
    private Consumer<TextFragment> onJump = fragment -> {
    };

    public TextContextPanel() {
        getStyleClass().addAll("card", "text-context-panel");
        setSpacing(12);
        setPadding(new Insets(16));

        heading.getStyleClass().add("heading-sm");
        emptyLabel.getStyleClass().add("text-muted");

        getChildren().addAll(heading, cardsHost);

        refreshText();
        LocalizationService.getInstance().addListener(locale -> refreshText());
    }

    public void refreshText() {
        LocalizationService i18n = LocalizationService.getInstance();
        heading.setText(i18n.get("analyse.keyParagraphs"));
        emptyLabel.setText(i18n.get("analyse.noDocumentRelevantParagraphs"));
        setResults(currentFragments);
    }

    public void setResults(List<TextFragment> fragments) {
        this.currentFragments = fragments == null ? List.of() : List.copyOf(fragments);
        cardsHost.getChildren().clear();
        if (currentFragments.isEmpty()) {
            showEmpty();
            return;
        }
        for (int i = 0; i < currentFragments.size(); i++) {
            cardsHost.getChildren().add(buildCard(i + 1, currentFragments.get(i)));
        }
    }

    private VBox buildCard(int rank, TextFragment fragment) {
        PassagePreview preview = new PassagePreview(
                "#" + rank + " · " + formatPageLabel(fragment.page(), fragment.paragraph()),
                truncateToSnippet(fragment.text()),
                () -> onJump.accept(fragment));
        return new ContextMatchCard(preview);
    }

    public void setOnJump(Consumer<TextFragment> handler) {
        this.onJump = handler == null ? fragment -> {
        } : handler;
    }

    private void showEmpty() {
        cardsHost.getChildren().setAll(emptyLabel);
    }

    private static String formatPageLabel(Integer page, Integer paragraph) {
        LocalizationService i18n = LocalizationService.getInstance();
        if (page != null && paragraph != null) {
            return i18n.get("analyse.pageAndParagraph")
                    .replace("{page}", String.valueOf(page))
                    .replace("{paragraph}", String.valueOf(paragraph));
        }
        if (page != null) {
            return i18n.get("analyse.page").replace("{page}", String.valueOf(page));
        }
        if (paragraph != null) {
            return i18n.get("analyse.paragraph").replace("{paragraph}", String.valueOf(paragraph));
        }
        return "";
    }

    private static String truncateToSnippet(String text) {
        if (text == null)
            return "";
        if (text.length() <= SNIPPET_CHAR_BUDGET)
            return text;

        int cut = text.lastIndexOf(' ', SNIPPET_CHAR_BUDGET);
        if (cut <= 0)
            cut = SNIPPET_CHAR_BUDGET;

        return text.substring(0, cut).stripTrailing() + "…";
    }
}
