package com.alexandria.view.components.compare_screen;

import com.alexandria.model.FileType;
import com.alexandria.service.analysis.SearchMatch;
import com.alexandria.view.components.analyse_screen.DocumentView;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.nio.file.Path;
import java.util.List;

public class ComparisonDocumentView extends VBox {

    private final DocumentView documentA = new DocumentView();
    private final DocumentView documentB = new DocumentView();

    private final Label documentALabel = new Label("Document A");
    private final Label documentBLabel = new Label("Document B");

    private final Label documentAName = new Label("No document loaded");
    private final Label documentBName = new Label("No document loaded");

    private final VBox documentARow;
    private final VBox documentBRow;

    public ComparisonDocumentView() {
        getStyleClass().add("comparison-document-view");

        setSpacing(12);
        setPadding(new Insets(0));
        setMinSize(0, 0);
        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        documentARow = buildDocumentRow(
                documentALabel,
                documentAName,
                documentA,
                "comparison-document-a");

        documentBRow = buildDocumentRow(
                documentBLabel,
                documentBName,
                documentB,
                "comparison-document-b");

        VBox.setVgrow(documentARow, Priority.ALWAYS);
        VBox.setVgrow(documentBRow, Priority.ALWAYS);

        getChildren().addAll(documentARow, documentBRow);

        clear();
    }

    private VBox buildDocumentRow(
            Label documentLabel,
            Label fileNameLabel,
            DocumentView documentView,
            String styleClass) {

        Label headerLabel = documentLabel;
        headerLabel.getStyleClass().add("comparison-document-label");

        fileNameLabel.getStyleClass().add("comparison-document-name");

        VBox header = new VBox(2, headerLabel, fileNameLabel);
        header.getStyleClass().add("comparison-document-header");
        header.setPadding(new Insets(8, 12, 8, 12));

        VBox row = new VBox(0, header, documentView);
        row.getStyleClass().add("comparison-document-row");
        row.getStyleClass().add(styleClass);

        row.setMinSize(0, 0);
        row.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        VBox.setVgrow(documentView, Priority.ALWAYS);

        return row;
    }

    public void loadDocumentA(
            String content,
            FileType fileType,
            Path sourcePath) {

        documentA.loadDocument(content, fileType, sourcePath);
        documentAName.setText(fileName(sourcePath));

        documentARow.setVisible(true);
        documentARow.setManaged(true);
    }

    public void loadDocumentB(
            String content,
            FileType fileType,
            Path sourcePath) {

        documentB.loadDocument(content, fileType, sourcePath);
        documentBName.setText(fileName(sourcePath));

        documentBRow.setVisible(true);
        documentBRow.setManaged(true);
    }

    public void loadDocuments(
            String contentA,
            FileType fileTypeA,
            Path sourcePathA,
            String contentB,
            FileType fileTypeB,
            Path sourcePathB) {

        loadDocumentA(contentA, fileTypeA, sourcePathA);
        loadDocumentB(contentB, fileTypeB, sourcePathB);
    }

    private String fileName(Path path) {
        if (path == null) {
            return "No document loaded";
        }

        Path fileName = path.getFileName();

        return fileName == null
                ? path.toString()
                : fileName.toString();
    }

    public DocumentView getDocumentA() {
        return documentA;
    }

    public DocumentView getDocumentB() {
        return documentB;
    }

    public void showSearchMatchesA(
            List<SearchMatch> matches,
            int activeIndex) {

        documentA.showSearchMatches(matches, activeIndex);
    }

    public void showSearchMatchesB(
            List<SearchMatch> matches,
            int activeIndex) {

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

    public void goToPageA(Integer page, Integer paragraph) {
        documentA.goToPage(page, paragraph);
    }

    public void goToPageB(Integer page, Integer paragraph) {
        documentB.goToPage(page, paragraph);
    }

    public void setOnQuotationRequested(
            java.util.function.BiFunction<String, String, Integer> handler) {

        documentA.setOnQuotationRequested(handler);
        documentB.setOnQuotationRequested(handler);
    }

    public void dispose() {
        documentA.dispose();
        documentB.dispose();

        documentAName.setText("No document loaded");
        documentBName.setText("No document loaded");
    }

    public void clear() {
        documentA.dispose();
        documentB.dispose();

        documentAName.setText("No document loaded");
        documentBName.setText("No document loaded");
    }
}
