package com.alexandria.view.screens;

import org.kordamp.ikonli.javafx.FontIcon;

import com.alexandria.view.components.shared.modal.Modal;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
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
        FontIcon searchIcon = new FontIcon("fas-search");
        searchIcon.getStyleClass().add("text-muted");
        TextField searchInput = new TextField();
        searchInput.setPromptText("Search library...");
        searchInput.getStyleClass().add("library-search-input");
        HBox.setHgrow(searchInput, Priority.ALWAYS);
        HBox searchBox = new HBox(8, searchIcon, searchInput);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.getStyleClass().addAll("text-field", "library-search-box");

        FlowPane cardsPane = new FlowPane(20, 20);
        cardsPane.getChildren().addAll(createProjectCard(), createNewProjectCard());

        content.getChildren().addAll(headerBox, sep, searchBox, cardsPane);
        getChildren().addAll(scrollPane, modal);
    }

    private VBox createProjectCard() {
        VBox card = new VBox(12);
        card.getStyleClass().add("card");
        card.setPrefWidth(280);
        card.setPrefHeight(200);

        Label tag = new Label("PDF");
        tag.getStyleClass().add("tags");
        Region hSpacer = new Region();
        HBox.setHgrow(hSpacer, Priority.ALWAYS);
        HBox header = new HBox(tag, hSpacer);

        Label title = new Label("Meditations");
        title.getStyleClass().add("heading-md");
        title.setWrapText(true);
        Label subtitle = new Label("meditations.pdf");
        subtitle.getStyleClass().add("text-muted");
        subtitle.setWrapText(true);
        VBox textContent = new VBox(4, title, subtitle);

        Region vSpacer = new Region();
        VBox.setVgrow(vSpacer, Priority.ALWAYS);

        Button editBtn = new Button("", new FontIcon("fas-edit"));
        editBtn.setTooltip(new Tooltip("Edit"));
        editBtn.getStyleClass().addAll("button", "secondary");

        Button delBtn = new Button("", new FontIcon("fas-trash-alt"));
        delBtn.setTooltip(new Tooltip("Delete"));
        delBtn.getStyleClass().addAll("button", "danger");

        Button openBtn = new Button("", new FontIcon("fas-folder-open"));
        openBtn.setTooltip(new Tooltip("Open with"));
        openBtn.getStyleClass().addAll("button", "primary");
        openBtn.setOnAction(e -> showOpenWithModal());
        
        HBox buttons = new HBox(6, editBtn, delBtn, openBtn);
        buttons.setAlignment(Pos.CENTER_LEFT);
        buttons.setOpacity(0.0);
        card.setOnMouseEntered(e -> buttons.setOpacity(1.0));
        card.setOnMouseExited(e -> buttons.setOpacity(0.0));

        Label fileType = new Label("Document");
        fileType.getStyleClass().addAll("text-muted", "mono-text");
        Region fSpacer = new Region();
        HBox.setHgrow(fSpacer, Priority.ALWAYS);
        Label date = new Label("Added 2h ago");
        date.getStyleClass().addAll("text-muted", "mono-text");
        HBox footer = new HBox(fileType, fSpacer, date);
        footer.setAlignment(Pos.CENTER_LEFT);

        card.getChildren().addAll(header, textContent, vSpacer, buttons, footer);
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