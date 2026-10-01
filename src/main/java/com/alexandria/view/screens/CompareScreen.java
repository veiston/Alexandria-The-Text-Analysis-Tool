package com.alexandria.view.screens;

import com.alexandria.model.FileType;
import com.alexandria.model.Text;
import com.alexandria.view.components.compare_screen.CompareHeader;
import com.alexandria.view.components.compare_screen.ComparisonDocumentView;
import com.alexandria.view.components.compare_screen.ComparisonSidePanel;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.File;
import java.nio.file.Path;
import java.util.function.BiFunction;

public class CompareScreen extends StackPane {

    private final CompareHeader header = new CompareHeader();
    private final ComparisonDocumentView documentView = new ComparisonDocumentView();
    private final ComparisonSidePanel comparisonSidePanel = new ComparisonSidePanel();

    private final VBox emptyState = buildEmptyState();
    private final BorderPane loadedState = new BorderPane();
    private final StackPane centerSwitcher = new StackPane();

    private BiFunction<String, String, Integer> onQuotationRequested = (text, location) -> null;
    private Runnable onSaveFindings = () -> {
    };

    public CompareScreen() {
        getStyleClass().add("compare-screen");

        emptyState.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        loadedState.setTop(header);
        loadedState.setCenter(buildBody());
        loadedState.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        getChildren().addAll(emptyState, loadedState);

        StackPane.setAlignment(emptyState, Pos.CENTER);
        StackPane.setAlignment(loadedState, Pos.CENTER);

        loadedState.setVisible(false);
        loadedState.setManaged(false);

        wireCallbacks();
    }

    private static final double SIDE_PANEL_WIDTH_RATIO = 0.360;

    private HBox buildBody() {
        centerSwitcher.getChildren().setAll(documentView);
        centerSwitcher.setMinSize(0, 0);
        centerSwitcher.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        HBox.setHgrow(centerSwitcher, Priority.ALWAYS);

        HBox body = new HBox(0, centerSwitcher, comparisonSidePanel);
        body.getStyleClass().add("compare-body");
        body.setFillHeight(true);
        body.setMinSize(0, 0);
        body.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        comparisonSidePanel.setMinWidth(320);
        comparisonSidePanel.setMaxWidth(640);
        comparisonSidePanel.prefWidthProperty().bind(
                body.widthProperty().multiply(SIDE_PANEL_WIDTH_RATIO));

        return body;
    }

    private void wireCallbacks() {
        header.setOnSave(() -> onSaveFindings.run());
        header.setOnViewChange(index -> {
            if (index == 1) {
                showQuotations();
            } else {
                showReader();
            }
        });

        documentView.setOnQuotationRequested(
                (text, location) -> onQuotationRequested.apply(text, location));
    }

    private void showReader() {
        centerSwitcher.getChildren().setAll(documentView);
    }

    private void showQuotations() {
        // TODO: quotations view for the compare screen.
        centerSwitcher.getChildren().setAll(documentView);
    }

    private VBox buildEmptyState() {
        Label title = new Label("No documents selected");
        title.getStyleClass().add("heading-lg");

        Label subtitle = new Label("Select two documents to compare them.");
        subtitle.getStyleClass().add("text-muted");

        VBox box = new VBox(8, title, subtitle);
        box.getStyleClass().add("empty-state");
        box.setAlignment(Pos.CENTER);
        return box;
    }

    public void loadDocuments(
            String documentATitle, String contentA, FileType fileTypeA, Path sourcePathA,
            String documentBTitle, String contentB, FileType fileTypeB, Path sourcePathB) {

        emptyState.setVisible(false);
        emptyState.setManaged(false);

        loadedState.setVisible(true);
        loadedState.setManaged(true);

        header.setSimilarity(null); // TODO: set from the comparison result
        header.resetToReader();
        showReader();

        documentView.loadDocuments(
                contentA, fileTypeA, sourcePathA,
                contentB, fileTypeB, sourcePathB);

        applyCss();
        layout();
    }

    public void loadTexts(Text textA, File fileA, Text textB, File fileB) {
        loadDocuments(
                displayTitle(textA), textA.getContent(), textA.getFileType(), toPath(fileA),
                displayTitle(textB), textB.getContent(), textB.getFileType(), toPath(fileB));

        documentView.setDocumentNames(textA.getFileName(), textB.getFileName());
    }

    private String displayTitle(Text text) {
        if (text.getTitle() != null && !text.getTitle().isBlank()) {
            return text.getTitle();
        }
        return text.getFileName();
    }

    private Path toPath(File file) {
        return file == null ? null : file.toPath();
    }

    public void setSimilarity(Double percent) {
        header.setSimilarity(percent);
    }

    public void clearComparison() {
        documentView.clear();
        comparisonSidePanel.clear();

        loadedState.setVisible(false);
        loadedState.setManaged(false);

        emptyState.setVisible(true);
        emptyState.setManaged(true);
    }

    public void dispose() {
        documentView.dispose();
        comparisonSidePanel.clear();
    }

    public ComparisonDocumentView getDocumentView() {
        return documentView;
    }

    public ComparisonSidePanel getComparisonSidePanel() {
        return comparisonSidePanel;
    }

    public CompareHeader getHeader() {
        return header;
    }

    public void setOnQuotationRequested(BiFunction<String, String, Integer> handler) {
        onQuotationRequested = handler == null ? (text, location) -> null : handler;
        documentView.setOnQuotationRequested(onQuotationRequested);
    }

    public void setOnSaveFindings(Runnable handler) {
        onSaveFindings = handler == null ? () -> {
        } : handler;
    }

    public void goToDocumentAPage(Integer page, Integer paragraph) {
        documentView.goToPageA(page, paragraph);
    }

    public void goToDocumentBPage(Integer page, Integer paragraph) {
        documentView.goToPageB(page, paragraph);
    }
}