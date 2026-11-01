package com.alexandria.view.components.compare_screen;

import com.alexandria.model.FileType;
import com.alexandria.service.LocalizationService;
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

    private final DocumentView documentA = new DocumentView();
    private final DocumentView documentB = new DocumentView();

    private final Label documentAName = new Label();
    private final Label documentBName = new Label();
    private final Label typeLabelA = new Label();
    private final Label typeLabelB = new Label();

    private boolean loadedA = false;
    private boolean loadedB = false;

    public ComparisonDocumentView() {
        getStyleClass().add("comparison-document-view");

        setSpacing(0);
        setMinSize(0, 0);
        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        // Comparison is read-only: no text selection and no citation/annotation popup.
        documentA.setSelectionEnabled(false);
        documentB.setSelectionEnabled(false);

        StackPane rowA = buildDocumentRow(typeLabelA, documentAName, documentA, "comparison-document-a");
        StackPane rowB = buildDocumentRow(typeLabelB, documentBName, documentB, "comparison-document-b");

        rowA.setPrefHeight(0);
        rowB.setPrefHeight(0);

        VBox.setVgrow(rowA, Priority.ALWAYS);
        VBox.setVgrow(rowB, Priority.ALWAYS);

        getChildren().addAll(rowA, rowB);

        refreshText();
        LocalizationService.getInstance().addListener(locale -> refreshText());
    }

    public void refreshText() {
        LocalizationService i18n = LocalizationService.getInstance();
        typeLabelA.setText(i18n.get("compare.documentA"));
        typeLabelB.setText(i18n.get("compare.documentB"));
        if (!loadedA) {
            documentAName.setText(i18n.get("compare.noDocumentLoaded"));
        }
        if (!loadedB) {
            documentBName.setText(i18n.get("compare.noDocumentLoaded"));
        }
    }

    private static final Insets NAME_TAG_MARGIN = new Insets(0, 14, 16, 0);

    private StackPane buildDocumentRow(
            Label typeLabel,
            Label fileNameLabel,
            DocumentView documentView,
            String styleClass) {

        typeLabel.getStyleClass().addAll("heading-sm", "comparison-document-label");
        typeLabel.setMinWidth(Label.USE_PREF_SIZE);

        fileNameLabel.getStyleClass().addAll("text-muted", "comparison-document-name");
        fileNameLabel.setTextOverrun(OverrunStyle.CENTER_ELLIPSIS);
        fileNameLabel.setMinWidth(0);
        fileNameLabel.setMaxWidth(Double.MAX_VALUE);

        HBox label = new HBox(6, typeLabel, fileNameLabel);
        label.getStyleClass().addAll("comparison-document-name-tag", "card");
        label.setAlignment(Pos.CENTER_LEFT);
        label.setMinWidth(0);
        label.setMaxWidth(Region.USE_PREF_SIZE);

        StackPane.setAlignment(label, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(label, NAME_TAG_MARGIN);

        StackPane stack = new StackPane(documentView, label);
        stack.getStyleClass().add(styleClass);
        stack.setMinSize(0, 0);
        stack.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        return stack;
    }

    public void loadDocuments(
            String contentA, FileType fileTypeA, Path sourcePathA,
            String contentB, FileType fileTypeB, Path sourcePathB) {

        loadedA = true;
        loadedB = true;
        documentA.loadDocument(contentA, fileTypeA, sourcePathA);
        documentB.loadDocument(contentB, fileTypeB, sourcePathB);
    }

    public void setDocumentNames(String nameA, String nameB) {
        setDocumentName(documentAName, nameA);
        setDocumentName(documentBName, nameB);
    }

    private void setDocumentName(Label label, String name) {
        label.setText(name);
        label.setTooltip(new Tooltip(name));
    }

    public void clear() {
        loadedA = false;
        loadedB = false;
        documentA.dispose();
        documentB.dispose();
        LocalizationService i18n = LocalizationService.getInstance();
        documentAName.setText(i18n.get("compare.noDocumentLoaded"));
        documentAName.setTooltip(null);
        documentBName.setText(i18n.get("compare.noDocumentLoaded"));
        documentBName.setTooltip(null);
    }

    public void dispose() {
        clear();
    }

    public void showSearchMatchesA(List<SearchMatch> matches, int activeIndex) {
        documentA.showSearchMatches(matches, activeIndex);
    }

    public void showSearchMatchesB(List<SearchMatch> matches, int activeIndex) {
        documentB.showSearchMatches(matches, activeIndex);
    }

    public void clearSearchHighlights() {
        documentA.clearSearchHighlights();
        documentB.clearSearchHighlights();
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

    public DocumentView getDocumentA() {
        return documentA;
    }

    public DocumentView getDocumentB() {
        return documentB;
    }
}
