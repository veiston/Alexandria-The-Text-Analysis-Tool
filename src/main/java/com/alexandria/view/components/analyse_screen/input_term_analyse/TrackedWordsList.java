package com.alexandria.view.components.analyse_screen.input_term_analyse;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

public class TrackedWordsList extends VBox {

    private final Label placeholderLabel =
            new Label("Tracked words list ");

    public TrackedWordsList() {
        getStyleClass().add("tracked-words-panel");
        setPadding(new Insets(8, 0, 0, 0));

        placeholderLabel.getStyleClass().add("text-muted");

        getChildren().add(placeholderLabel);
    }

    /** Adds or updates the row for this word (bar + hover, same visual as TextTermFrequencyPanel). Called automatically on search, not manually requested. */
    public void addOrUpdate(String word, int count) {
    }

    /** x button click — removes this word's row from the list. */
    public void remove(String word) {
    }

    /**
     * The single style class used to highlight whichever term is
     * currently active. There is only one — this is not per-word.
     */
    public String activeHighlightColorClass() {
        return null;
    }

    /**
     * Marks which tracked word is "current" right now (the one being
     * searched / stepped through). Only this word's occurrences should be
     * highlighted in the document, using activeHighlightColorClass().
     * Pass null to clear the active term.
     */
    public void setActiveTerm(String word) {
    }

    /** Row click — opens the term detail modal for this word. */
    public void setOnInfo(Consumer<String> handler) {
    }

    /** x button icon click — fires when the user asks to stop tracking this word. */
    public void setOnRemove(Consumer<String> handler) {
    }

    /**
     * "Go to" hyperlink click. Caller is responsible for jumping the
     * document view to the term, highlighting it, syncing the search box
     * (silently — see class doc), and enabling arrow-key navigation between
     * its occurrences.
     */
    public void setOnGoTo(Consumer<String> handler) {
    }

    public void reset() {
    }
}