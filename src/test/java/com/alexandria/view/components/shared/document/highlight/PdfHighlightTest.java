package com.alexandria.view.components.shared.document.highlight;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javafx.scene.shape.Rectangle;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

public class PdfHighlightTest {

    private PDDocument document;

    @Before
    public void setUp() throws IOException {
        document = createSinglePageDocument("The quick brown fox jumps over the lazy dog");
    }

    @After
    public void tearDown() throws IOException {
        document.close();
    }

    @Test
    public void findHighlights_findsSingleOccurrence_caseInsensitive() throws IOException {
        List<Rectangle> results = PdfHighlight.findHighlights(
                document, 0, "QUICK", 72f, PdfHighlight.SEARCH_STYLE_CLASS);

        assertEquals(1, results.size());
        assertTrue(results.get(0).getStyleClass().contains(PdfHighlight.SEARCH_STYLE_CLASS));
    }

    @Test
    public void findHighlights_findsMultipleOccurrences() throws IOException {
        try (PDDocument doc = createSinglePageDocument("fox fox fox")) {
            List<Rectangle> results = PdfHighlight.findHighlights(doc, 0, "fox", 72f, null);
            assertEquals(3, results.size());
        }
    }

    @Test
    public void findHighlights_noMatch_returnsEmpty() throws IOException {
        assertTrue(PdfHighlight.findHighlights(document, 0, "nonexistentword", 72f, null).isEmpty());
    }

    @Test
    public void findHighlights_blankOrNullTerm_returnsEmpty() throws IOException {
        assertTrue(PdfHighlight.findHighlights(document, 0, "", 72f, null).isEmpty());
        assertTrue(PdfHighlight.findHighlights(document, 0, "   ", 72f, null).isEmpty());
        assertTrue(PdfHighlight.findHighlights(document, 0, null, 72f, null).isEmpty());
    }

    @Test
    public void findHighlights_rectangleHasNonZeroDimensions() throws IOException {
        List<Rectangle> results = PdfHighlight.findHighlights(document, 0, "brown", 72f, null);

        assertEquals(1, results.size());
        assertTrue(results.get(0).getWidth() > 0);
        assertTrue(results.get(0).getHeight() > 0);
    }

    @Test
    public void findSearchHighlights_appliesSearchStyleClass() throws IOException {
        List<Rectangle> results = PdfHighlight.findSearchHighlights(document, 0, "fox", 72f);
        assertTrue(results.get(0).getStyleClass().contains(PdfHighlight.SEARCH_STYLE_CLASS));
    }

    @Test
    public void findQuotationHighlights_appliesQuotationStyleClass() throws IOException {
        List<Rectangle> results = PdfHighlight.findQuotationHighlights(document, 0, "lazy dog", 72f);
        assertTrue(results.get(0).getStyleClass().contains(PdfHighlight.QUOTATION_STYLE_CLASS));
    }

    static PDDocument createSinglePageDocument(String text) throws IOException {
        PDDocument doc = new PDDocument();
        PDPage page = new PDPage(PDRectangle.LETTER);
        doc.addPage(page);

        try (PDPageContentStream stream = new PDPageContentStream(doc, page)) {
            stream.beginText();
            stream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
            stream.newLineAtOffset(72, 700);
            stream.showText(text);
            stream.endText();
        }

        return doc;
    }
}
