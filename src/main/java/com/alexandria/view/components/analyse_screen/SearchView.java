package com.alexandria.view.components.analyse_screen;

import com.alexandria.view.components.analyse_screen.input_term_analyse.TrackedWordsList;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/** TODO: real search box + wiring. */
public class SearchView extends VBox {

    private final TrackedWordsList trackedWordsList = new TrackedWordsList();

    public SearchView() {
        getStyleClass().add("search-view");
        Label placeholder = new Label("Search Term Frequency");
        placeholder.getStyleClass().add("text-muted");
        getChildren().addAll(placeholder, trackedWordsList);
    }

    public TrackedWordsList getTrackedWordsList() { return trackedWordsList; }
    public void reset() { trackedWordsList.reset(); }
}