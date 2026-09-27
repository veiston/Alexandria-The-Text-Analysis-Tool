package com.alexandria.view.screens;

import org.kordamp.ikonli.javafx.FontIcon;

import com.alexandria.view.components.shared.Card;
import com.alexandria.view.components.shared.SearchInput;
import com.alexandria.view.components.shared.modal.Modal;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class LibraryScreen extends StackPane {

    private final VBox content = new VBox(24);
    private final ScrollPane scrollPane = new ScrollPane(content);
    private final Modal modal = new Modal();

    public LibraryScreen() {
        getStyleClass().add("library-screen");
        content.setPadding(new Insets(40));
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("transparent-scroll-pane");

        // Header
        Label title = new Label("Research Library");
        title.getStyleClass().add("heading-xl");
        Label subtitle = new Label("Manage, analyze, and organize your academic texts and datasets.");
        subtitle.getStyleClass().add("text-muted");
        VBox headerBox = new VBox(8, title, subtitle);

        // Separator
        javafx.scene.control.Separator sep = new javafx.scene.control.Separator();
        
        // Toolbar
        SearchInput searchInput = new SearchInput("Search library...");
        searchInput.getStyleClass().add("library-search-box");
        HBox.setHgrow(searchInput, Priority.ALWAYS);

        FlowPane cardsPane = new FlowPane(20, 20);
        cardsPane.getChildren().addAll(createProjectCard(), createNewProjectCard());

        content.getChildren().addAll(headerBox, sep, searchInput, cardsPane);
        getChildren().addAll(scrollPane, modal);
    }

    private Card createProjectCard() {
        Card card = new Card("Meditations");
        card.setTypeText("PDF");
        card.setSourceText("meditations.pdf");
        card.setFooterText("Added 2h ago");

        Button editBtn = new Button("", new FontIcon("fas-edit"));
        editBtn.setTooltip(new Tooltip("Edit"));
        editBtn.getStyleClass().addAll("button", "secondary");

        Button openBtn = new Button("Open with", new FontIcon("fas-folder-open"));
        openBtn.getStyleClass().addAll("button", "secondary");
        openBtn.setOnAction(e -> showOpenWithModal());

        card.setSecondaryActions(editBtn, openBtn);
        return card;
    }

    private VBox createNewProjectCard() {
        FontIcon addIcon = new FontIcon("fas-plus-circle");
        addIcon.setIconSize(24);
        addIcon.getStyleClass().add("text-muted");

        Label title = new Label("Start New Analysis");
        title.getStyleClass().add("heading-md");
        
        Label subtitle = new Label("From library documents");
        subtitle.getStyleClass().add("text-muted");

        VBox card = new VBox(12, addIcon, title, subtitle);
        card.getStyleClass().addAll("card", "card-dashed");
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(280);
        card.setPrefHeight(200);
        return card;
    }

    /* Modal composition */

    private void showOpenWithModal() {
        VBox modalContent = new VBox(20);
        modalContent.getStyleClass().add("modal-card");
        modalContent.setPadding(new Insets(24));
        modalContent.setPrefWidth(350);

        Label title = new Label("Open Project");
        title.getStyleClass().add("heading-lg");

        VBox options = new VBox(12);

        Button analysisBtn = new Button("Open in Text Analysis");
        analysisBtn.getStyleClass().addAll("button", "primary");
        analysisBtn.setMaxWidth(Double.MAX_VALUE);
        analysisBtn.setOnAction(e -> modal.hide());

        Button compareBtn = new Button("Compare with another file...");
        compareBtn.getStyleClass().addAll("button", "secondary");
        compareBtn.setMaxWidth(Double.MAX_VALUE);
        compareBtn.setOnAction(e -> showCompareSelection());

        options.getChildren().addAll(analysisBtn, compareBtn);
        modalContent.getChildren().addAll(title, options);
        
        modal.show(modalContent);
    }

    private void showCompareSelection() {
        VBox modalContent = new VBox(20);
        modalContent.getStyleClass().add("modal-card");
        modalContent.setPadding(new Insets(24));
        modalContent.setPrefWidth(350);

        Label title = new Label("Select File for Comparison");
        title.getStyleClass().add("heading-lg");

        ComboBox<String> fileSelect = new ComboBox<>();
        fileSelect.setPromptText("Choose file...");
        fileSelect.setMaxWidth(Double.MAX_VALUE);
        fileSelect.getStyleClass().add("text-field");

        HBox buttons = new HBox(12);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        
        Button cancel = new Button("Cancel");
        cancel.getStyleClass().addAll("button", "secondary");
        cancel.setOnAction(e -> modal.hide());
        
        Button confirm = new Button("Compare");
        confirm.getStyleClass().addAll("button", "primary");
        confirm.setOnAction(e -> modal.hide());
        
        buttons.getChildren().addAll(cancel, confirm);

        modalContent.getChildren().addAll(title, fileSelect, buttons);
        modal.show(modalContent);
    }
}