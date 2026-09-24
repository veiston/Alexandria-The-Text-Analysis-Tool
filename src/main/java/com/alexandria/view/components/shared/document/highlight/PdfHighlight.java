package com.alexandria.view.components.shared.document.highlight;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class PdfHighlight {

    public static final String SEARCH_STYLE_CLASS = "pdf-search-highlight";
    public static final String QUOTATION_STYLE_CLASS = "pdf-quotation-highlight";

    private PdfHighlight() {
    }

    public static List<Rectangle> findHighlights(
            PDDocument document,
            int zeroBasedPage,
            String text,
            float renderDpi,
            String styleClass) throws IOException {

        List<Rectangle> results = new ArrayList<>();

        if (text == null || text.isBlank()) {
            return results;
        }

        String needle = text.strip().toLowerCase(Locale.ROOT);
        float scale = renderDpi / 72f;

        List<TextPosition> positions = new ArrayList<>();
        StringBuilder extractedText = new StringBuilder();

        PDFTextStripper stripper = new PDFTextStripper() {
            @Override
            protected void writeString(
                    String string,
                    List<TextPosition> textPositions) {
                extractedText.append(string.toLowerCase(Locale.ROOT));
                positions.addAll(textPositions);
            }
        };

        stripper.setStartPage(zeroBasedPage + 1);
        stripper.setEndPage(zeroBasedPage + 1);
        stripper.getText(document);

        String haystack = extractedText.toString();

        int index = haystack.indexOf(needle);

        while (index >= 0) {
            int end = index + needle.length();

            if (end <= positions.size()) {
                results.add(
                        buildRectangle(
                                positions.subList(index, end),
                                scale,
                                styleClass));
            }

            index = haystack.indexOf(needle, index + 1);

        }

        return results;
    }

    public static List<Rectangle> findSearchHighlights(
            PDDocument document,
            int zeroBasedPage,
            String searchTerm,
            float renderDpi) throws IOException {
        return findHighlights(
                document,
                zeroBasedPage,
                searchTerm,
                renderDpi,
                SEARCH_STYLE_CLASS);
    }

    public static List<Rectangle> findQuotationHighlights(
            PDDocument document,
            int zeroBasedPage,
            String quotedText,
            float renderDpi) throws IOException {
        return findHighlights(
                document,
                zeroBasedPage,
                quotedText,
                renderDpi,
                QUOTATION_STYLE_CLASS);
    }

    private static Rectangle buildRectangle(
            List<TextPosition> run,
            float scale,
            String styleClass) {
        float minX = Float.MAX_VALUE;
        float minY = Float.MAX_VALUE;
        float maxX = 0;
        float maxY = 0;

        for (TextPosition position : run) {
            minX = Math.min(
                    minX,
                    position.getXDirAdj());

            minY = Math.min(
                    minY,
                    position.getYDirAdj() - position.getHeightDir());

            maxX = Math.max(
                    maxX,
                    position.getXDirAdj() + position.getWidthDirAdj());

            maxY = Math.max(
                    maxY,
                    position.getYDirAdj());
        }

        Rectangle rectangle = new Rectangle(
                minX * scale,
                minY * scale,
                (maxX - minX) * scale,
                (maxY - minY) * scale);

        rectangle.setFill(Color.TRANSPARENT);

        if (styleClass != null && !styleClass.isBlank()) {
            rectangle.getStyleClass().add(styleClass);
        }

        rectangle.setMouseTransparent(true);

        return rectangle;

    }
}