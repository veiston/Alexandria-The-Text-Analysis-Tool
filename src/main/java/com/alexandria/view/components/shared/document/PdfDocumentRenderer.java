package com.alexandria.view.components.shared.document;

import com.alexandria.view.components.shared.document.highlight.PdfHighlight;
import com.alexandria.view.components.shared.document.highlight.PdfTextLayout;
import com.alexandria.view.components.shared.selection.QuotationSelectionPopup;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class PdfDocumentRenderer {

    private static final float BASE_DPI = 144f;
    private static final double BASE_PAGE_WIDTH = 620.0;
    private static final double PAGE_MARGIN = 30.0;

    private static final String LIVE_SELECTION_STYLE_CLASS = "pdf-selection-rect";

    private final BorderPane root = new BorderPane();
    private final ScrollPane scrollPane = new ScrollPane();
    private final StackPane pageHost = new StackPane();
    private final ImageView imageView = new ImageView();
    private final Label errorLabel = new Label();

    private final Pane selectionOverlay = new Pane();
    private final StackPane contentStack = new StackPane();

    private PDDocument document;
    private PDFRenderer renderer;

    private int currentPage = 0;
    private double zoom = 1.0;

    private BufferedImage currentImage;

    private PdfTextLayout currentLayout;

    private double imageOffsetX = 0;
    private double imageOffsetY = 0;
    private double displayScaleX = 1;
    private double displayScaleY = 1;

    private boolean dragActive = false;
    private int dragStartGlyphIndex = -1;

    private QuotationSelectionPopup quotationPopup;

    private final Map<Integer, List<double[]>> quotationRectsByPage = new HashMap<>();

    private Consumer<Integer> onVisiblePageChanged = ignored -> {
    };

    private BiConsumer<String, String> onQuotationRequested = (quotationText, location) -> {
    };

    public PdfDocumentRenderer() {
        root.getStyleClass().add("pdf-renderer");

        pageHost.setAlignment(Pos.CENTER);
        pageHost.setMinSize(0, 0);
        pageHost.getStyleClass().add("pdf-page-host");

        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);

        imageView.setCursor(Cursor.TEXT);

        pageHost.getChildren().add(imageView);

        imageView.setOnMousePressed(this::beginSelection);
        imageView.setOnMouseDragged(this::updateSelection);
        imageView.setOnMouseReleased(this::endSelection);

        scrollPane.setContent(pageHost);
        scrollPane.setFitToWidth(false);
        scrollPane.setFitToHeight(false);
        scrollPane.setPannable(true);
        scrollPane.getStyleClass().add("viewer-scroll");

        selectionOverlay.setPickOnBounds(false);

        selectionOverlay.prefWidthProperty().bind(contentStack.widthProperty());
        selectionOverlay.prefHeightProperty().bind(contentStack.heightProperty());

        contentStack.getChildren().addAll(scrollPane, selectionOverlay);

        root.setCenter(contentStack);

        errorLabel.getStyleClass().add("pdf-error");
        errorLabel.setWrapText(true);
        errorLabel.setMaxWidth(500);
        errorLabel.setVisible(false);
    }

    public void setPdf(Path path) {
        closeDocument();

        if (path == null || !Files.isRegularFile(path) || !Files.isReadable(path)) {
            showError("The PDF file could not be opened:\n"+ path);

            return;
        }

        try {
            document = Loader.loadPDF(path.toFile());

            renderer = new PDFRenderer(document);
            renderer.setSubsamplingAllowed(true);

            currentPage = 0;
            zoom = 1.0;

            renderCurrentPage();

        } catch (IOException | RuntimeException e) {
            showError("Could not open PDF:\n" + e.getMessage());
        }
    }

    private void renderCurrentPage() {
        if (renderer == null || document == null) {
            return;
        }

        hideQuotationPopup();
        dragActive = false;

        try {
            currentImage = renderer.renderImageWithDPI(
                    currentPage,
                    BASE_DPI,
                    ImageType.RGB);

            imageView.setImage(
                    SwingFXUtils.toFXImage(
                            currentImage,
                            null));

            try {
                currentLayout = PdfTextLayout.forPage(document, currentPage, BASE_DPI);
            } catch (IOException | RuntimeException e) {
                currentLayout = null;
            }

            updateImageSize();

            errorLabel.setVisible(false);
            restorePageHost();

            onVisiblePageChanged.accept(
                    currentPage + 1);

        } catch (IOException | RuntimeException e) {
            showError(
                    "Could not render PDF page "
                            + (currentPage + 1)
                            + ":\n"
                            + e.getMessage());
        }
    }

    private double[] rasterToDisplay(double[] rasterBounds) {
        return new double[]{
                imageOffsetX + rasterBounds[0] * displayScaleX,
                imageOffsetY + rasterBounds[1] * displayScaleY,
                rasterBounds[2] * displayScaleX,
                rasterBounds[3] * displayScaleY
        };
    }

    private void convertRasterRectangleToDisplay(Rectangle rectangle) {
        double x = rectangle.getX();
        double y = rectangle.getY();
        double w = rectangle.getWidth();
        double h = rectangle.getHeight();

        rectangle.setX(imageOffsetX + x * displayScaleX);
        rectangle.setY(imageOffsetY + y * displayScaleY);
        rectangle.setWidth(w * displayScaleX);
        rectangle.setHeight(h * displayScaleY);
    }

    private void restoreQuotationHighlights() {
        for (double[] bounds : quotationRectsByPage.getOrDefault(currentPage, List.of())) {
            drawQuotationRect(bounds);
        }
    }

    private void drawQuotationRect(double[] rasterBounds) {
        double[] display = rasterToDisplay(rasterBounds);

        Rectangle rectangle = new Rectangle(display[0], display[1], display[2], display[3]);
        rectangle.setManaged(false);
        rectangle.setFill(Color.TRANSPARENT);
        rectangle.getStyleClass().add(PdfHighlight.QUOTATION_STYLE_CLASS);
        rectangle.setMouseTransparent(true);

        pageHost.getChildren().add(rectangle);

        imageView.toBack();
    }

    public void highlight(
            String text,
            String styleClass) {

        if (document == null || currentImage == null) {
            return;
        }

        try {
            List<Rectangle> highlights = PdfHighlight.findHighlights(
                    document,
                    currentPage,
                    text,
                    BASE_DPI,
                    styleClass);

            for (Rectangle rectangle : highlights) {
                convertRasterRectangleToDisplay(rectangle);
                rectangle.setManaged(false);
                rectangle.setMouseTransparent(true);
            }

            pageHost.getChildren().addAll(highlights);

            imageView.toBack();

        } catch (IOException | RuntimeException e) {
            // Highlighting is supplementary UI; don't destroy the PDF view.
        }
    }

    public void highlightSearch(String searchTerm) {
        clearHighlights();

        highlight(
                searchTerm,
                PdfHighlight.SEARCH_STYLE_CLASS);

        restoreQuotationHighlights();
    }

    public void highlightQuotation(String quotedText) {
        clearHighlights();

        highlight(
                quotedText,
                PdfHighlight.QUOTATION_STYLE_CLASS);
    }

    public void clearHighlights() {
        pageHost.getChildren().removeIf(
                node -> node instanceof Rectangle rectangle
                        && !rectangle.getStyleClass().contains(LIVE_SELECTION_STYLE_CLASS));
    }

    private void clearLiveSelectionRects() {
        pageHost.getChildren().removeIf(
                node -> node instanceof Rectangle rectangle
                        && rectangle.getStyleClass().contains(LIVE_SELECTION_STYLE_CLASS));
    }

    private void updateImageSize() {
        if (currentImage == null) {
            return;
        }

        double baseScale = BASE_PAGE_WIDTH
                / currentImage.getWidth();

        double pageWidth = BASE_PAGE_WIDTH * zoom;

        double pageHeight = currentImage.getHeight()
                * baseScale
                * zoom;

        double viewportWidth = scrollPane.getViewportBounds().getWidth();

        double viewportHeight = scrollPane.getViewportBounds().getHeight();

        double hostWidth = Math.max(
                pageWidth + PAGE_MARGIN * 2,
                viewportWidth);

        double hostHeight = Math.max(
                pageHeight + PAGE_MARGIN * 2,
                viewportHeight);

        pageHost.setPrefWidth(hostWidth);
        pageHost.setPrefHeight(hostHeight);

        pageHost.setMinWidth(hostWidth);
        pageHost.setMinHeight(hostHeight);

        imageView.setFitWidth(pageWidth);
        imageView.setFitHeight(pageHeight);

        imageOffsetX = (hostWidth - pageWidth) / 2.0;
        imageOffsetY = (hostHeight - pageHeight) / 2.0;

        displayScaleX = pageWidth / currentImage.getWidth();
        displayScaleY = pageHeight / currentImage.getHeight();

        clearHighlights();
        clearLiveSelectionRects();
        restoreQuotationHighlights();

        Platform.runLater(this::centerPage);
    }

    private void centerPage() {
        if (currentImage == null) {
            return;
        }

        double viewportWidth = scrollPane.getViewportBounds().getWidth();
        double viewportHeight = scrollPane.getViewportBounds().getHeight();
        double contentWidth = pageHost.getWidth();
        double contentHeight = pageHost.getHeight();

        double maxX = Math.max(
                0,
                contentWidth - viewportWidth);

        double maxY = Math.max(
                0,
                contentHeight - viewportHeight);

        scrollPane.setHvalue(
                maxX <= 0
                        ? 0.5
                        : 0.5);

        scrollPane.setVvalue(
                maxY <= 0
                        ? 0.5
                        : 0.5);
    }

    private void showError(String message) {
        errorLabel.setText(
                message == null
                        ? "Unknown PDF error."
                        : message);

        errorLabel.setVisible(true);

        VBox errorBox = new VBox(12, errorLabel);

        errorBox.setAlignment(Pos.CENTER);

        root.setCenter(errorBox);
    }

    private void restorePageHost() {
        if (root.getCenter() != contentStack) {
            root.setCenter(contentStack);
        }
    }

    public Node getNode() {
        return root;
    }

    public void goToPage(
            Integer page,
            Integer paragraph) {

        if (document == null
                || page == null
                || document.getNumberOfPages() == 0) {
            return;
        }

        currentPage = Math.max(
                0,
                Math.min(
                        page - 1,
                        document.getNumberOfPages() - 1));

        renderCurrentPage();
    }

    public void setZoom(double value) {
        zoom = Math.max(
                0.5,
                Math.min(2.0, value));

        updateImageSize();
    }

    public int getPageCount() {
        return document == null
                ? 0
                : document.getNumberOfPages();
    }

    public void setOnVisiblePageChanged(
            Consumer<Integer> handler) {

        onVisiblePageChanged = handler == null
                ? ignored -> {
                }
                : handler;
    }

    public void setOnQuotationRequested(BiConsumer<String, String> handler) {
        onQuotationRequested = handler == null
                ? (quotationText, location) -> {
                }
                : handler;
    }

    private void beginSelection(MouseEvent event) {
        hideQuotationPopup();
        clearLiveSelectionRects();

        if (currentImage == null || currentLayout == null || currentLayout.isEmpty()) {
            return;
        }

        scrollPane.setPannable(false);
        event.consume();

        dragStartGlyphIndex = glyphIndexAt(event);
        dragActive = dragStartGlyphIndex >= 0;

        updateLiveSelectionRects(dragStartGlyphIndex, dragStartGlyphIndex);
    }

    private void updateSelection(MouseEvent event) {
        if (!dragActive || currentLayout == null) {
            return;
        }

        event.consume();

        int currentIndex = glyphIndexAt(event);

        updateLiveSelectionRects(dragStartGlyphIndex, currentIndex);
    }

    private void endSelection(MouseEvent event) {
        scrollPane.setPannable(true);

        if (!dragActive) {
            return;
        }

        dragActive = false;
        event.consume();

        if (currentLayout == null || document == null) {
            clearLiveSelectionRects();
            return;
        }

        int endIndex = glyphIndexAt(event);
        int startIndex = dragStartGlyphIndex;

        if (startIndex < 0 || endIndex < 0 || startIndex == endIndex) {
            // A plain click (no real drag across text) isn't a selection.
            clearLiveSelectionRects();
            return;
        }

        String selectedText = currentLayout.textFor(startIndex, endIndex);

        if (selectedText.isBlank()) {
            clearLiveSelectionRects();
            return;
        }

        List<double[]> rasterRects = currentLayout.selectionRectangles(startIndex, endIndex);

        if (rasterRects.isEmpty()) {
            clearLiveSelectionRects();
            return;
        }

        String location = "page:" + (currentPage + 1);

        showQuotationPopup(
                event.getScreenX(),
                event.getScreenY(),
                selectedText,
                location,
                rasterRects);
    }

    /** Converts an ImageView-local mouse event to a glyph index via {@link #currentLayout}. */
    private int glyphIndexAt(MouseEvent event) {
        if (currentLayout == null || displayScaleX <= 0 || displayScaleY <= 0) {
            return -1;
        }

        double rasterX = event.getX() / displayScaleX;
        double rasterY = event.getY() / displayScaleY;

        return currentLayout.nearestGlyphIndex(rasterX, rasterY);
    }

    /** Rebuilds the temporary (blue) selection rectangles for the drag in progress. */
    private void updateLiveSelectionRects(int startIndex, int endIndex) {
        clearLiveSelectionRects();

        if (currentLayout == null || startIndex < 0 || endIndex < 0) {
            return;
        }

        for (double[] rasterRect : currentLayout.selectionRectangles(startIndex, endIndex)) {
            double[] display = rasterToDisplay(rasterRect);

            Rectangle rectangle = new Rectangle(display[0], display[1], display[2], display[3]);
            rectangle.setManaged(false);
            rectangle.setMouseTransparent(true);
            rectangle.getStyleClass().add(LIVE_SELECTION_STYLE_CLASS);

            pageHost.getChildren().add(rectangle);
        }

        imageView.toBack();
    }

    private void showQuotationPopup(
            double screenX,
            double screenY,
            String selectedText,
            String location,
            List<double[]> rasterRects) {

        int page = currentPage;

        quotationPopup = new QuotationSelectionPopup(() -> {
            quotationRectsByPage
                    .computeIfAbsent(page, ignored -> new ArrayList<>())
                    .addAll(rasterRects);

            for (double[] rect : rasterRects) {
                drawQuotationRect(rect);
            }

            onQuotationRequested.accept(selectedText, location);

            hideQuotationPopup();
            clearLiveSelectionRects();
        });

        quotationPopup.showAt(selectionOverlay, screenX, screenY);
    }

    private void hideQuotationPopup() {
        if (quotationPopup != null) {
            quotationPopup.hide();
            quotationPopup = null;
        }
    }

    public void dispose() {
        closeDocument();
    }

    private void closeDocument() {
        hideQuotationPopup();
        clearLiveSelectionRects();
        scrollPane.setPannable(true);
        quotationRectsByPage.clear();

        dragActive = false;
        dragStartGlyphIndex = -1;
        currentLayout = null;

        if (document != null) {
            try {
                document.close();
            } catch (IOException ignored) {
            }
        }

        document = null;
        renderer = null;
        currentImage = null;

        imageView.setImage(null);
        clearHighlights();

        currentPage = 0;
        zoom = 1.0;
    }
}