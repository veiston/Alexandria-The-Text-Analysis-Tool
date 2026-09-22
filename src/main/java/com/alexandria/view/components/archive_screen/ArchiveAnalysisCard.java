package com.alexandria.view.components.archive_screen;

import com.alexandria.model.ArchiveTermAnalysis;
import com.alexandria.model.ArchiveTextAnalysis;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ArchiveAnalysisCard extends VBox {
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("d MMM uuuu");
    private final Button deleteButton = createDeleteButton();
    private final Button openButton = createOpenButton();

    public ArchiveAnalysisCard(ArchiveTextAnalysis analysis) {

        createCard(
                "Text analysis",
                analysis.getProjectTitle(),
                analysis.getSourceFileName(),
                null,
                analysis.getCreatedAt());
    }

    public ArchiveAnalysisCard(ArchiveTermAnalysis analysis) {

        createCard(
                "Term analysis",
                analysis.getProjectTitle(),
                analysis.getSourceFileName(),
                analysis.getTerm(),
                analysis.getCreatedAt());
    }

    private void createCard(
            String analysisType,
            String projectTitle,
            String sourceFileName,
            String term,
            LocalDateTime createdAt) {

        getStyleClass().add("card");
        setSpacing(12);
        setPrefWidth(280);
        setPrefHeight(200);

        Label type = new Label(analysisType);
        type.getStyleClass().add("tags");

        AnchorPane cardHeader = new AnchorPane(type, deleteButton);
        cardHeader.setPrefHeight(20);
        AnchorPane.setTopAnchor(type, 0.0);
        AnchorPane.setLeftAnchor(type, 0.0);
        AnchorPane.setTopAnchor(deleteButton, -10.0);
        AnchorPane.setRightAnchor(deleteButton, 0.0);

        String titleText = projectTitle;
        if (titleText == null || titleText.isBlank()) {
            titleText = sourceFileName;
        }

        Label title = new Label(titleText);
        title.getStyleClass().add("heading-md");
        title.setWrapText(true);

        VBox analysisDetails = new VBox(4, title);

        if (term != null) {
            Label termLabel = new Label("Term: " + term);
            termLabel.getStyleClass().add("archive-term-preview");
            termLabel.setWrapText(true);
            analysisDetails.getChildren().add(termLabel);
        }

        Label source = new Label(sourceFileName);
        source.getStyleClass().add("text-muted");
        source.setWrapText(true);
        analysisDetails.getChildren().add(source);

        Label savedAt = new Label("Saved " + formatDate(createdAt));
        savedAt.getStyleClass().addAll("text-muted", "mono-text");
        Region dateSpacer = new Region();
        HBox.setHgrow(dateSpacer, Priority.ALWAYS);
        HBox footer = new HBox(dateSpacer, savedAt);
        footer.setAlignment(Pos.CENTER_LEFT);

        Region buttonSpacer = new Region();
        VBox.setVgrow(buttonSpacer, Priority.ALWAYS);

        getChildren().addAll(cardHeader, analysisDetails, buttonSpacer, footer, openButton);
    }

    private Button createDeleteButton() {
        FontIcon icon = new FontIcon(FontAwesomeSolid.TRASH_ALT);
        icon.setIconSize(12);

        Button delete = new Button();
        delete.setGraphic(icon);
        delete.setTooltip(new Tooltip("Delete"));
        delete.getStyleClass().addAll("button", "archive-icon-button");
        return delete;
    }

    private Button createOpenButton() {
        Button open = new Button("Open");
        open.getStyleClass().addAll("button", "primary");
        open.setMaxWidth(Double.MAX_VALUE);
        return open;
    }

    public Button getDeleteButton() {
        return deleteButton;
    }

    public Button getOpenButton() {
        return openButton;
    }

    private String formatDate(LocalDateTime date) {
        return date == null ? "unknown date" : DATE_FORMAT.format(date);
    }
}
