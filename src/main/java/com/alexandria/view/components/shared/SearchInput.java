package com.alexandria.view.components.shared;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.function.Consumer;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class SearchInput extends HBox {
    private final TextField input = new TextField();
    private Consumer<String> onSearch = term -> {
    };

    public SearchInput(String promptText) {
        FontIcon searchIcon = new FontIcon(FontAwesomeSolid.SEARCH);
        searchIcon.getStyleClass().add("text-muted");

        input.setPromptText(promptText);
        input.getStyleClass().add("search-input-field");
        HBox.setHgrow(input, Priority.ALWAYS);

        getStyleClass().add("search-input");
        setAlignment(Pos.CENTER_LEFT);
        setSpacing(8);
        getChildren().addAll(searchIcon, input);
    }

    public String getText() {
        return input.getText();
    }

    public StringProperty textProperty() {
        return input.textProperty();
    }

    public void setOnSearch(Consumer<String> handler) {
        onSearch = handler == null ? term -> {
        } : handler;

        input.setOnAction(event -> {
            onSearch.accept(input.getText());
        });
    }

}
