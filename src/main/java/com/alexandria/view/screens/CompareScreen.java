package com.alexandria.view.screens;

import com.alexandria.model.FileType;
import com.alexandria.view.components.compare_screen.CompareHeader;
import com.alexandria.view.components.compare_screen.ComparisonDocumentView;
import com.alexandria.view.components.compare_screen.ComparisonSidePanel;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.nio.file.Path;
import java.util.function.BiFunction;

public class CompareScreen extends StackPane {

    private final CompareHeader header = new CompareHeader();

    private final ComparisonDocumentView documentView = new ComparisonDocumentView();

    private final ComparisonSidePanel comparisonSidePanel = new ComparisonSidePanel();

    private final VBox emptyState = buildEmptyState();

    private final BorderPane loadedState = new BorderPane();

    private final StackPane centerSwitcher = new StackPane();

    private BiFunction<String, String, Integer> onQuotationRequested = (quotationText, location) -> null;

    private Runnable onSaveFindings = () -> {
    };

    public CompareScreen() {
        getStyleClass().add("compare-screen");

        emptyState.setMaxSize(
                Double.MAX_VALUE,
                Double.MAX_VALUE);

        loadedState.setTop(header);
        loadedState.setCenter(buildBody());

        loadedState.setMaxSize(
                Double.MAX_VALUE,
                Double.MAX_VALUE);

        getChildren().addAll(
                emptyState,
                loadedState);

        StackPane.setAlignment(
                emptyState,
                Pos.CENTER);

        StackPane.setAlignment(
                loadedState,
                Pos.CENTER);

        loadedState.setVisible(false);
        loadedState.setManaged(false);

        wireCallbacks();
    }

    private HBox buildBody() {

        centerSwitcher.getChildren().setAll(
                documentView);

        centerSwitcher.setMinSize(0, 0);
        centerSwitcher.setMaxSize(
                Double.MAX_VALUE,
                Double.MAX_VALUE);

        HBox.setHgrow(
                centerSwitcher,
                Priority.ALWAYS);

        comparisonSidePanel.setMinWidth(220);
        comparisonSidePanel.setPrefWidth(260);
        comparisonSidePanel.setMaxWidth(340);

        HBox body = new HBox(
                10,
                centerSwitcher,
                comparisonSidePanel);

        body.getStyleClass().add(
                "compare-body");

        body.setFillHeight(true);

        body.setMinSize(0, 0);
        body.setMaxSize(
                Double.MAX_VALUE,
                Double.MAX_VALUE);

        return body;
    }

    private void wireCallbacks() {

        header.setOnSave(
                () -> onSaveFindings.run());

        header.setOnReader(
                () -> showReader());

        header.setOnQuotations(
                () -> showQuotations());

        documentView.setOnQuotationRequested(
                (quotationText, location) -> onQuotationRequested.apply(
                        quotationText,
                        location));
    }

    private void showReader() {
        centerSwitcher.getChildren().setAll(
                documentView);
    }

    private void showQuotations() {
        // Placeholder for the future quotations comparison view.
        //
        // For now we keep the document reader visible.
        centerSwitcher.getChildren().setAll(
                documentView);
    }

    private VBox buildEmptyState() {

        Label title = new Label("No documents selected");

        title.getStyleClass().add(
                "heading-lg");

        Label subtitle = new Label(
                "Select two documents to compare them.");

        subtitle.getStyleClass().add(
                "text-muted");

        VBox box = new VBox(
                8,
                title,
                subtitle);

        box.getStyleClass().add(
                "compare-empty-state");

        box.setAlignment(
                Pos.CENTER);

        return box;
    }

    public void loadDocuments(
            String documentATitle,
            String contentA,
            FileType fileTypeA,
            Path sourcePathA,
            String documentBTitle,
            String contentB,
            FileType fileTypeB,
            Path sourcePathB) {

        header.setTitle(
                documentATitle,
                documentBTitle);

        header.resetToReader();

        documentView.loadDocuments(
                contentA,
                fileTypeA,
                sourcePathA,
                contentB,
                fileTypeB,
                sourcePathB);

        emptyState.setVisible(false);
        emptyState.setManaged(false);

        loadedState.setVisible(true);
        loadedState.setManaged(true);

        showReader();
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

    public void setOnQuotationRequested(
            BiFunction<String, String, Integer> handler) {

        onQuotationRequested = handler == null
                ? (quotationText, location) -> null
                : handler;

        documentView.setOnQuotationRequested(
                onQuotationRequested);
    }

    public void setOnSaveFindings(
            Runnable handler) {

        onSaveFindings = handler == null
                ? () -> {
                }
                : handler;
    }

    public void goToDocumentAPage(
            Integer page,
            Integer paragraph) {

        documentView.goToPageA(
                page,
                paragraph);
    }

    public void goToDocumentBPage(
            Integer page,
            Integer paragraph) {

        documentView.goToPageB(
                page,
                paragraph);
    }
}
