package com.alexandria.view.components.compare_screen;

import com.alexandria.model.FileType;
import com.alexandria.service.analysis.SearchMatch;
import com.alexandria.view.components.shared.document.DocumentView;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.nio.file.Path;
import java.util.List;

public class ComparisonDocumentView extends VBox {

    private static final String NO_DOCUMENT = "No document loaded";

    private final DocumentView documentA = new DocumentView();
    private final DocumentView documentB = new DocumentView();

    private final Label documentAName = new Label(NO_DOCUMENT);
    private final Label documentBName = new Label(NO_DOCUMENT);

    public ComparisonDocumentView() {

        getStyleClass().add("comparison-document-view");

        setSpacing(0);
        setMinSize(0, 0);
        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        // Comparison is read-only: no text selection and no citation/annotation popup.
        documentA.setSelectionEnabled(false);
        documentB.setSelectionEnabled(false);

        StackPane rowA = buildDocumentRow(
                "Doc A:", documentAName, documentA, "comparison-document-a");

        StackPane rowB = buildDocumentRow(
                "Doc B:", documentBName, documentB, "comparison-document-b");

        rowA.setPrefHeight(0);
        rowB.setPrefHeight(0);

        VBox.setVgrow(rowA, Priority.ALWAYS);
        VBox.setVgrow(rowB, Priority.ALWAYS);

        getChildren().addAll(rowA, rowB);
    }

    private static final Insets NAME_TAG_MARGIN = new Insets(0, 14, 16, 0);

    private StackPane buildDocumentRow(
            String documentLabel,
            Label fileNameLabel,
            DocumentView documentView,
            String styleClass) {

        Label typeLabel = new Label(documentLabel);
        typeLabel.getStyleClass().addAll("heading-sm", "comparison-document-label");
        typeLabel.setMinWidth(Label.USE_PREF_SIZE);

        fileNameLabel.getStyleClass().addAll("text-muted", "comparison-document-name");
        fileNameLabel.setTextOverrun(OverrunStyle.CENTER_ELLIPSIS);
        fileNameLabel.setMinWidth(0);
        HBox.setHgrow(fileNameLabel, Priority.ALWAYS);

        HBox nameTag = new HBox(6, typeLabel, fileNameLabel);
        nameTag.setAlignment(Pos.CENTER_LEFT);
        nameTag.getStyleClass().add("comparison-document-tag");
        nameTag.setPickOnBounds(false);
        nameTag.setMaxHeight(Region.USE_PREF_SIZE);

        StackPane row = new StackPane(documentView, nameTag);
        row.getStyleClass().addAll("comparison-document-row", styleClass);
        row.setMinSize(0, 0);
        row.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        nameTag.maxWidthProperty().bind(row.widthProperty().multiply(0.4));

        StackPane.setAlignment(nameTag, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(nameTag, NAME_TAG_MARGIN);

        return row;
    }

    public void loadDocumentA(String content, FileType fileType, Path sourcePath) {
        documentA.loadDocument(content, fileType, sourcePath);
        updateName(documentAName, sourcePath);
    }

    public void loadDocumentB(String content, FileType fileType, Path sourcePath) {
        documentB.loadDocument(content, fileType, sourcePath);
        updateName(documentBName, sourcePath);
    }

    public void loadDocuments(
            String contentA, FileType fileTypeA, Path sourcePathA,
            String contentB, FileType fileTypeB, Path sourcePathB) {

        loadDocumentA(contentA, fileTypeA, sourcePathA);
        loadDocumentB(contentB, fileTypeB, sourcePathB);
    }

    public void setDocumentNames(String nameA, String nameB) {
        if (nameA != null && !nameA.isBlank()) {
            documentAName.setText(nameA);
        }
        if (nameB != null && !nameB.isBlank()) {
            documentBName.setText(nameB);
        }
    }

    private void updateName(Label label, Path path) {
        label.setText(fileName(path));
        label.setTooltip(path == null ? null : new Tooltip(path.toString()));
    }

    private String fileName(Path path) {
        if (path == null) {
            return NO_DOCUMENT;
        }
        Path name = path.getFileName();
        return name == null ? path.toString() : name.toString();
    }

    public DocumentView getDocumentA() {
        return documentA;
    }

    public DocumentView getDocumentB() {
        return documentB;
    }

    public void goToPageA(Integer page, Integer paragraph) {
        documentA.goToPage(page, paragraph);
    }

    public void goToPageB(Integer page, Integer paragraph) {
        documentB.goToPage(page, paragraph);
    }

    public void showSearchMatchesA(List<SearchMatch> matches, int activeIndex) {
        documentA.showSearchMatches(matches, activeIndex);
    }

    public void showSearchMatchesB(List<SearchMatch> matches, int activeIndex) {
        documentB.showSearchMatches(matches, activeIndex);
    }

    public void showSearchMatchA(SearchMatch match) {
        documentA.showSearchMatch(match);
    }

    public void showSearchMatchB(SearchMatch match) {
        documentB.showSearchMatch(match);
    }

    public void clearSearchHighlightsA() {
        documentA.clearSearchHighlights();
    }

    public void clearSearchHighlightsB() {
        documentB.clearSearchHighlights();
    }

    public void dispose() {
        documentA.dispose();
        documentB.dispose();
        updateName(documentAName, null);
        updateName(documentBName, null);
    }

    public void clear() {
        dispose();
    }
}