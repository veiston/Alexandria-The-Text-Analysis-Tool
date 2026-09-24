package com.alexandria.view.components.shared.document.highlight;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public final class PdfTextLayout {
    private static final double LINE_HEIGHT_PADDING_RATIO = 1.20;

    private record Glyph(
            double x,
            double y,
            double width,
            double height,
            double spaceWidth,
            String unicode,
            int line) {
    }

    private final List<Glyph> glyphs;
    private final double[] lineTop;
    private final double[] lineBottom;

    private PdfTextLayout(List<Glyph> glyphs, double[] lineTop, double[] lineBottom) {
        this.glyphs = glyphs;
        this.lineTop = lineTop;
        this.lineBottom = lineBottom;
    }

    public static PdfTextLayout forPage(
            PDDocument document,
            int zeroBasedPage,
            float renderDpi) throws IOException {

        List<TextPosition> positions = new ArrayList<>();

        PDFTextStripper stripper = new PDFTextStripper() {
            @Override
            protected void writeString(String string, List<TextPosition> textPositions) {
                positions.addAll(textPositions);
            }
        };

        stripper.setStartPage(zeroBasedPage + 1);
        stripper.setEndPage(zeroBasedPage + 1);
        stripper.getText(document);

        float scale = renderDpi / 72f;
        List<TextPosition> sorted = new ArrayList<>(positions);
        sorted.sort(Comparator.comparingDouble(TextPosition::getYDirAdj)
                .thenComparingDouble(TextPosition::getXDirAdj));

        List<Glyph> result = new ArrayList<>();

        int line = -1;
        Float previousLineY = null;

        for (TextPosition p : sorted) {
            float y = p.getYDirAdj();
            float tolerance = p.getHeightDir() * 0.5f;

            if (previousLineY == null || Math.abs(y - previousLineY) > tolerance) {
                line++;
                previousLineY = y;
            }

            result.add(new Glyph(
                    p.getXDirAdj() * scale,
                    (p.getYDirAdj() - p.getHeightDir()) * scale,
                    p.getWidthDirAdj() * scale,
                    p.getHeightDir() * scale,
                    p.getWidthOfSpace() * scale,
                    p.getUnicode(),
                    line));
        }

        result.sort(Comparator.comparingInt(Glyph::line)
                .thenComparingDouble(Glyph::x));

        int lineCount = result.isEmpty()
                ? 0
                : result.get(result.size() - 1).line() + 1;

        double[] lineTop = new double[lineCount];
        double[] lineBottom = new double[lineCount];

        Arrays.fill(lineTop, Double.MAX_VALUE);
        Arrays.fill(lineBottom, -Double.MAX_VALUE);

        for (Glyph g : result) {
            lineTop[g.line()] = Math.min(lineTop[g.line()], g.y());
            lineBottom[g.line()] = Math.max(lineBottom[g.line()], g.y() + g.height());
        }

        return new PdfTextLayout(result, lineTop, lineBottom);
    }

    public boolean isEmpty() {
        return glyphs.isEmpty();
    }

    public int nearestGlyphIndex(double x, double y) {
        if (glyphs.isEmpty()) {
            return -1;
        }

        int best = 0;
        double bestDistance = Double.MAX_VALUE;

        for (int i = 0; i < glyphs.size(); i++) {
            Glyph g = glyphs.get(i);

            double centerX = g.x() + g.width() / 2.0;
            double centerY = g.y() + g.height() / 2.0;

            double dx = centerX - x;
            double dy = centerY - y;

            double distance = dx * dx + dy * dy;

            if (distance < bestDistance) {
                bestDistance = distance;
                best = i;
            }
        }

        return best;
    }

    public List<double[]> selectionRectangles(int indexA, int indexB) {
        List<double[]> rects = new ArrayList<>();

        if (glyphs.isEmpty()) {
            return rects;
        }

        int start = Math.max(0, Math.min(indexA, indexB));
        int end = Math.min(glyphs.size() - 1, Math.max(indexA, indexB));

        int currentLine = -1;
        double minX = 0;
        double maxX = 0;
        boolean open = false;

        for (int i = start; i <= end; i++) {
            Glyph g = glyphs.get(i);

            if (g.line() != currentLine) {
                if (open) {
                    rects.add(lineRect(currentLine, minX, maxX));
                }

                currentLine = g.line();
                minX = g.x();
                maxX = g.x() + g.width();
                open = true;

            } else {
                minX = Math.min(minX, g.x());
                maxX = Math.max(maxX, g.x() + g.width());
            }
        }

        if (open) {
            rects.add(lineRect(currentLine, minX, maxX));
        }

        return rects;
    }

    private double[] lineRect(int line, double minX, double maxX) {
        double top = lineTop[line];
        double bottom = lineBottom[line];

        double height = bottom - top;
        double padding = height * LINE_HEIGHT_PADDING_RATIO / 2.0;

        return new double[]{
                minX,
                top - padding,
                maxX - minX,
                height + padding * 2
        };
    }

      public String textFor(int indexA, int indexB) {
        if (glyphs.isEmpty()) {
            return "";
        }

        int start = Math.max(0, Math.min(indexA, indexB));
        int end = Math.min(glyphs.size() - 1, Math.max(indexA, indexB));

        StringBuilder sb = new StringBuilder();
        Glyph previous = null;

        for (int i = start; i <= end; i++) {
            Glyph g = glyphs.get(i);

            if (previous != null) {
                if (g.line() != previous.line()) {
                    sb.append('\n');
                } else {
                    double gap = g.x() - (previous.x() + previous.width());

                    double threshold = previous.spaceWidth() > 0
                            ? previous.spaceWidth() * 0.5
                            : previous.height() * 0.3;

                    if (gap > threshold) {
                        sb.append(' ');
                    }
                }
            }

            sb.append(g.unicode());
            previous = g;
        }

        return sb.toString().strip();
    }
}