package com.alexandria.view.components.analyse_screen;

import com.alexandria.view.components.analyse_screen.input_term_analyse.TrackedWordsList;
import com.alexandria.view.components.shared.SearchInput;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

public class SearchView extends VBox {

    private final SearchInput searchInput;
    private final Button previousButton;
    private final Button nextButton;
    private final TrackedWordsList trackedWordsList;

    private SearchCallback onSearch;
    private Runnable onPreviousMatch;
    private Runnable onNextMatch;

    public SearchView() {
        getStyleClass().add("search-view");

        searchInput = new SearchInput("Search Term Frequency");

        previousButton = createIconButton(FontAwesomeSolid.CHEVRON_UP);
        nextButton = createIconButton(FontAwesomeSolid.CHEVRON_DOWN);

        trackedWordsList = new TrackedWordsList();

        HBox navigationButtons = new HBox(
                previousButton,
                nextButton);

        navigationButtons.setAlignment(Pos.CENTER_RIGHT);
        navigationButtons.getStyleClass().add("search-navigation-buttons");

        HBox searchControls = new HBox(
                searchInput,
                navigationButtons);

        searchControls.setAlignment(Pos.CENTER_LEFT);
        searchControls.setPadding(new Insets(4, 0, 8, 0));

        HBox.setHgrow(searchInput, Priority.ALWAYS);

        getChildren().addAll(
                searchControls,
                trackedWordsList);

        wireEvents();
    }

    private void wireEvents() {
        searchInput.setOnSearch(term -> {
            if (onSearch != null) {
                onSearch.onSearch(term);
            }
        });

        previousButton.setOnAction(event -> {
            if (onPreviousMatch != null) {
                onPreviousMatch.run();
            }
        });

        nextButton.setOnAction(event -> {
            if (onNextMatch != null) {
                onNextMatch.run();
            }
        });
    }

    private Button createIconButton(FontAwesomeSolid icon) {
        FontIcon fontIcon = new FontIcon(icon);
        fontIcon.getStyleClass().add("text-muted");

        Button button = new Button();
        button.setGraphic(fontIcon);

        button.getStyleClass().add("icon-button");
        button.getStyleClass().add("search-navigation-button");

        return button;
    }

    public void setOnSearch(SearchCallback callback) {
        this.onSearch = callback;
    }

    public void setOnPreviousMatch(Runnable callback) {
        this.onPreviousMatch = callback;
    }

    public void setOnNextMatch(Runnable callback) {
        this.onNextMatch = callback;
    }

    public String getSearchText() {
        return searchInput.getText();
    }

    public SearchInput getSearchInput() {
        return searchInput;
    }

    public TrackedWordsList getTrackedWordsList() {
        return trackedWordsList;
    }

    public void reset() {
        trackedWordsList.reset();
    }

    @FunctionalInterface
    public interface SearchCallback {
        void onSearch(String term);
    }
}