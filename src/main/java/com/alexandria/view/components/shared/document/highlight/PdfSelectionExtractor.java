package com.alexandria.view.components.shared.document.highlight;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class PdfSelectionExtractor {

    private PdfSelectionExtractor() {
    }

    public static String extractText(PDDocument document, int zeroBasedPage, double x, double y, double w, double h)
            throws IOException {

        double minX = x, maxX = x + w, minY = y, maxY = y + h;
        List<TextPosition> hits = new ArrayList<>();

        PDFTextStripper stripper = new PDFTextStripper() {
            @Override
            protected void writeString(String string, List<TextPosition> textPositions) {
                for (TextPosition p : textPositions) {
                    double px = p.getXDirAdj();
                    double py = p.getYDirAdj() - p.getHeightDir();
                    if (px >= minX && px <= maxX && py + p.getHeightDir() >= minY && py <= maxY) {
                        hits.add(p);
                    }
                }
            }
        };
        stripper.setStartPage(zeroBasedPage + 1);
        stripper.setEndPage(zeroBasedPage + 1);
        stripper.getText(document);

        return joinWithSpacing(hits);
    }

    private static String joinWithSpacing(List<TextPosition> hits) {
        StringBuilder sb = new StringBuilder();
        TextPosition previous = null;

        for (TextPosition p : hits) {
            if (previous != null && needsSpaceBefore(previous, p)) {
                sb.append(' ');
            }

            sb.append(p.getUnicode());
            previous = p;
        }

        return sb.toString().strip();
    }

    private static boolean needsSpaceBefore(TextPosition previous, TextPosition next) {
        float verticalTolerance = previous.getHeightDir() * 0.5f;

        if (Math.abs(next.getYDirAdj() - previous.getYDirAdj()) > verticalTolerance) {
            return true;
        }

        float gap = next.getXDirAdj() - (previous.getXDirAdj() + previous.getWidthDirAdj());

        float spaceWidth = previous.getWidthOfSpace();
        float threshold = spaceWidth > 0 ? spaceWidth * 0.5f : previous.getWidthDirAdj() * 0.3f;

        return gap > threshold;
    }
}