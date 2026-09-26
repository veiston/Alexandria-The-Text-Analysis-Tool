package com.alexandria.view.components.analyse_screen;

import com.alexandria.model.FileType;
import com.alexandria.view.components.shared.document.PdfDocumentRenderer;
import com.alexandria.view.components.shared.document.TextDocumentRenderer;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.nio.file.Path;
import java.util.function.BiFunction;

public class DocumentView extends BorderPane {

        private PdfDocumentRenderer pdfRenderer;
        private TextDocumentRenderer textRenderer;
        private boolean showingPdf;

        private final Label pageLabel = new Label("Page 1 / 1");
        private final Button previous = new Button("‹");
        private final Button next = new Button("›");
        private final Button zoomOut = new Button("−");
        private final Button zoomIn = new Button("+");

        private int currentPage = 1;
        private double zoom = 1.0;
        private BiFunction<String, String, Integer> onQuotationRequested = (quotationText, location) -> null;

        public DocumentView() {
                getStyleClass().add("document-view");
                configureToolbar();
                setMinSize(0, 0);
                setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        }

        private void configureToolbar() {
                previous.getStyleClass().add("viewer-nav-button");
                next.getStyleClass().add("viewer-nav-button");
                zoomOut.getStyleClass().add("viewer-nav-button");
                zoomIn.getStyleClass().add("viewer-nav-button");
                pageLabel.getStyleClass().add("viewer-page-label");
                previous.setOnAction(e -> previousPage());
                next.setOnAction(e -> nextPage());
                zoomOut.setOnAction(e -> setZoom(zoom - 0.1));
                zoomIn.setOnAction(e -> setZoom(zoom + 0.1));
                previous.setDisable(true);
                next.setDisable(true);

                HBox zoomControls = new HBox(8, zoomOut, zoomIn);
                HBox pageControls = new HBox(8, previous, pageLabel, next);
                pageControls.setAlignment(Pos.CENTER);

                HBox leftSpacer = spacer();
                HBox rightSpacer = spacer();
                HBox toolbarContent = new HBox(12, zoomControls, leftSpacer, pageControls, rightSpacer);
                toolbarContent.setAlignment(Pos.CENTER);

                ToolBar toolbar = new ToolBar(toolbarContent);
                toolbar.getStyleClass().add("viewer-toolbar");

                setBottom(toolbar);
        }

        private HBox spacer() {
                HBox box = new HBox();
                HBox.setHgrow(box, Priority.ALWAYS);

                return box;
        }

        public void loadDocument(String content, FileType fileType, Path sourcePath) {
                dispose();

                currentPage = 1;
                zoom = 1.0;
                showingPdf = fileType == FileType.PDF && sourcePath != null;

                if (showingPdf) {
                        pdfRenderer = new PdfDocumentRenderer();
                        pdfRenderer.setOnVisiblePageChanged(this::onPageChanged);
                        pdfRenderer.setOnQuotationRequested(onQuotationRequested);
                        pdfRenderer.setPdf(sourcePath);

                        setCenter(pdfRenderer.getNode());

                } else {
                        textRenderer = new TextDocumentRenderer(content);
                        textRenderer.setOnVisiblePageChanged(this::onPageChanged);
                        textRenderer.setOnQuotationRequested(onQuotationRequested);
                        setCenter(textRenderer.getNode());
                }

                BorderPane.setAlignment(getCenter(), Pos.CENTER);

                setZoom(zoom);
                updatePageInformation();
        }

        private void onPageChanged(int page) {
                currentPage = Math.max(1, page);
                updatePageInformation();
        }

        private void previousPage() {
                int total = getPageCount();

                if (total <= 0) {
                        return;
                }

                int target = Math.max(1, currentPage - 1);

                if (target != currentPage) {
                        goToPage(target, null);
                }
        }

        private void nextPage() {
                int total = getPageCount();
                if (total <= 0) {
                        return;
                }

                int target = Math.min(total, currentPage + 1);
                if (target != currentPage) {
                        goToPage(target, null);
                }
        }

        public void goToPage(Integer page, Integer paragraph) {

                if (showingPdf && pdfRenderer != null) {
                        pdfRenderer.goToPage(page, paragraph);

                } else if (textRenderer != null) {
                        textRenderer.goToPage(page, paragraph);
                }
        }

        private void setZoom(double value) {
                zoom = Math.max(0.75, Math.min(2.0, value));

                if (showingPdf && pdfRenderer != null) {
                        pdfRenderer.setZoom(zoom);
                } else if (textRenderer != null) {
                        textRenderer.setZoom(zoom);
                }
        }

        private int getPageCount() {
                if (showingPdf && pdfRenderer != null) {
                        return pdfRenderer.getPageCount();
                }

                if (textRenderer != null) {
                        return textRenderer.getPageCount();
                }

                return 0;
        }

        private void updatePageInformation() {
                int total = Math.max(1, getPageCount());
                currentPage = Math.max(1, Math.min(currentPage, total));

                pageLabel.setText("Page " + currentPage + " / " + total);
                previous.setDisable(total <= 1 || currentPage <= 1);
                next.setDisable(total <= 1 || currentPage >= total);
        }

        public void highlightPdfSearch(String searchTerm) {
                if (showingPdf && pdfRenderer != null) {
                        pdfRenderer.highlightSearch(searchTerm);
                }
        }

        public void highlightPdfQuotation(String quotedText) {
                if (showingPdf && pdfRenderer != null) {
                        pdfRenderer.highlightQuotation(quotedText);
                }
        }

        public void clearPdfHighlights() {
                if (showingPdf && pdfRenderer != null) {
                        pdfRenderer.clearHighlights();
                }
        }

        public void setOnQuotationRequested(BiFunction<String, String, Integer> handler) {
                onQuotationRequested = handler == null
                                ? (quotationText, location) -> null
                                : handler;

                if (pdfRenderer != null) {
                        pdfRenderer.setOnQuotationRequested(onQuotationRequested);
                }

                if (textRenderer != null) {
                        textRenderer.setOnQuotationRequested(onQuotationRequested);
                }
        }

        public void removeQuotationHighlight(int quotationId) {
                if (showingPdf && pdfRenderer != null) {
                        pdfRenderer.removeQuotationHighlight(quotationId);

                } else if (textRenderer != null) {
                        textRenderer.removeQuotationHighlight(quotationId);
                }
        }

        public void dispose() {
                if (pdfRenderer != null) {
                        pdfRenderer.dispose();
                        pdfRenderer = null;
                }

                if (textRenderer != null) {
                        textRenderer.dispose();
                        textRenderer = null;
                }

                setCenter(null);

                showingPdf = false;
                currentPage = 1;
                zoom = 1.0;

                pageLabel.setText("Page 1 / 1");

                previous.setDisable(true);
                next.setDisable(true);
        }
}