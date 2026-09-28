package com.alexandria.view.components.shared.document.highlight;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

public class PdfTextLayoutTest {

    private PDDocument document;

    @Before
    public void setUp() throws IOException {
        document = PdfHighlightTest.createSinglePageDocument("hello world");
    }

    @After
    public void tearDown() throws IOException {
        document.close();
    }

    @Test
    public void forPage_extractsGlyphs() throws IOException {
        assertFalse(PdfTextLayout.forPage(document, 0, 72f).isEmpty());
    }

    @Test
    public void forPage_emptyPage_isEmpty() throws IOException {
        try (PDDocument empty = new PDDocument()) {
            empty.addPage(new PDPage(PDRectangle.LETTER));
            assertTrue(PdfTextLayout.forPage(empty, 0, 72f).isEmpty());
        }
    }

    @Test
    public void nearestGlyphIndex_returnsValidIndex() throws IOException {
        PdfTextLayout layout = PdfTextLayout.forPage(document, 0, 72f);
        assertTrue(layout.nearestGlyphIndex(0, 0) >= 0);
    }

    @Test
    public void nearestGlyphIndex_emptyLayout_returnsNegativeOne() throws IOException {
        try (PDDocument empty = new PDDocument()) {
            empty.addPage(new PDPage(PDRectangle.LETTER));
            PdfTextLayout layout = PdfTextLayout.forPage(empty, 0, 72f);
            assertEquals(-1, layout.nearestGlyphIndex(0, 0));
        }
    }

    @Test
    public void textFor_wholeRange_reconstructsText() throws IOException {
        PdfTextLayout layout = PdfTextLayout.forPage(document, 0, 72f);
        // "hello world" is 11 characters, indices 0..10
        String text = layout.textFor(0, 10).replaceAll("\\s+", " ").trim();
        assertEquals("hello world", text.toLowerCase());
    }

    @Test
    public void selectionRectangles_wholeRange_returnsPositiveSizedRectangles() throws IOException {
        PdfTextLayout layout = PdfTextLayout.forPage(document, 0, 72f);
        List<double[]> rects = layout.selectionRectangles(0, 10);

        assertFalse(rects.isEmpty());
        for (double[] rect : rects) {
            assertEquals(4, rect.length);
            assertTrue(rect[2] > 0);
            assertTrue(rect[3] > 0);
        }
    }
}