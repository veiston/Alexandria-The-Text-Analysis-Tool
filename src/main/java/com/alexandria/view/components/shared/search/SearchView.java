package com.alexandria.view.components.shared.search;


import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import com.alexandria.service.analysis.SearchSettings;
import com.alexandria.view.components.shared.SearchInput;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class SearchView extends VBox {

    private final SearchInput searchInput;
    private final Button previousButton;
    private final Button nextButton;
    private final TrackedWordsList trackedWordsList;

    private final CheckBox fuzzyCheckBox = new CheckBox("Fuzzy");
    private final CheckBox caseCheckBox = new CheckBox("Case sensitive");
    private final CheckBox wholeWordCheckBox = new CheckBox("Whole word");
    private final CheckBox stopWordsCheckBox = new CheckBox("Stop words");

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
        searchControls.setPadding(new Insets(4, 0, 4, 0));

        HBox.setHgrow(searchInput, Priority.ALWAYS);

        FlowPane searchOptions = new FlowPane(
                8,
                4,
                fuzzyCheckBox,
                caseCheckBox,
                wholeWordCheckBox,
                stopWordsCheckBox);

        getChildren().addAll(
                searchControls,
                searchOptions,
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

        CheckBox[] options = {
                fuzzyCheckBox,
                caseCheckBox,
                wholeWordCheckBox,
                stopWordsCheckBox
        };

        for (CheckBox option : options) {
            option.getStyleClass().add("text-muted");
            option.setOnAction(e -> {
                String term = searchInput.getText();
                if (onSearch != null && term != null) {
                    if (!term.isBlank()) {
                        onSearch.onSearch(term);
                    }
                }
            });
        }
    }

    public SearchSettings getSearchSettings() {
        return new SearchSettings(
                caseCheckBox.isSelected(),
                fuzzyCheckBox.isSelected(),
                wholeWordCheckBox.isSelected(),
                stopWordsCheckBox.isSelected());
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