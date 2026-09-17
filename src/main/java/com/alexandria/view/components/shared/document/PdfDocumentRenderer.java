package com.alexandria.view.components.shared.document;

import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;

public class PdfDocumentRenderer {
    private static final float BASE_DPI = 144f;
    private static final double BASE_PAGE_WIDTH = 620.0;

    private final BorderPane root = new BorderPane();
    private final ScrollPane scrollPane = new ScrollPane();
    private final StackPane pageHost = new StackPane();
    private final ImageView imageView = new ImageView();
    private final Label errorLabel = new Label();

    private PDDocument document;
    private PDFRenderer renderer;

    private int currentPage = 0;
    private double zoom = 1.0;
    private BufferedImage currentImage;

    private Consumer<Integer> onVisiblePageChanged = ignored -> {
    };

    public PdfDocumentRenderer() {
        root.getStyleClass().add("pdf-renderer");

        pageHost.setAlignment(Pos.TOP_CENTER);
        pageHost.setMinSize(0, 0);
        pageHost.getStyleClass().add("pdf-page-host");

        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        pageHost.getChildren().add(imageView);

        scrollPane.setContent(pageHost);
        scrollPane.setFitToWidth(false);
        scrollPane.setFitToHeight(false);
        scrollPane.setPannable(true);
        scrollPane.getStyleClass().add("viewer-scroll");

        root.setCenter(scrollPane);

        errorLabel.getStyleClass().add("text-muted");
        errorLabel.setWrapText(true);
        errorLabel.setMaxWidth(500);
        errorLabel.setVisible(false);
    }

    public void setPdf(Path path) {
        closeDocument();

        if (path == null || !Files.isRegularFile(path) || !Files.isReadable(path)) {
            showError("The PDF file could not be opened:\n" + path);
            return;
        }

        try {
            document = Loader.loadPDF(path.toFile());
            renderer = new PDFRenderer(document);
            renderer.setSubsamplingAllowed(true);
            currentPage = 0;
            renderCurrentPage();
        } catch (IOException | RuntimeException e) {
            showError("Could not open PDF:\n" + e.getMessage());
        }
    }

    private void renderCurrentPage() {
        if (renderer == null || document == null)
            return;

        try {
            currentImage = renderer.renderImageWithDPI(currentPage, BASE_DPI, ImageType.RGB);
            imageView.setImage(SwingFXUtils.toFXImage(currentImage, null));
            updateImageSize();

            errorLabel.setVisible(false);
            restorePageHost();

            onVisiblePageChanged.accept(currentPage + 1);

        } catch (IOException | RuntimeException e) {
            showError("Could not render PDF page " + (currentPage + 1) + ":\n" + e.getMessage());
        }
    }

    private void updateImageSize() {
        if (currentImage == null)
            return;

        double baseScale = BASE_PAGE_WIDTH / currentImage.getWidth();
        double width = BASE_PAGE_WIDTH * zoom;
        double height = currentImage.getHeight() * baseScale * zoom;

        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        pageHost.setPrefWidth(width);
        pageHost.setPrefHeight(height);
        pageHost.setMinWidth(width);
        pageHost.setMinHeight(height);
    }

    private void showError(String message) {
        errorLabel.setText(message == null ? "Unknown PDF error." : message);
        errorLabel.setVisible(true);

        VBox errorBox = new VBox(12, errorLabel);
        errorBox.setAlignment(Pos.CENTER);
        root.setCenter(errorBox);
    }

    private void restorePageHost() {
        if (root.getCenter() != scrollPane)
            root.setCenter(scrollPane);
    }

    public Node getNode() {
        return root;
    }

    public void goToPage(Integer page, Integer paragraph) {
        if (document == null || page == null || document.getNumberOfPages() == 0)
            return;

        currentPage = Math.max(0, Math.min(page - 1, document.getNumberOfPages() - 1));
        renderCurrentPage();
    }

    public void setZoom(double value) {
        zoom = Math.max(0.5, Math.min(2.0, value));
        updateImageSize();
    }

    public int getPageCount() {
        return document == null ? 0 : document.getNumberOfPages();
    }

    public void setOnVisiblePageChanged(Consumer<Integer> handler) {
        onVisiblePageChanged = handler == null ? ignored -> {
        } : handler;
    }

    public void dispose() {
        closeDocument();
    }

    private void closeDocument() {
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
        currentPage = 0;
    }
}