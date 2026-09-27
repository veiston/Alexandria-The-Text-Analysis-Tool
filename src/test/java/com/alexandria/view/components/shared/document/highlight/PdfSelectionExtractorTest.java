package com.alexandria.view.components.shared.document.highlight;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class PdfSelectionExtractorTest {

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
    public void extractText_boxCoveringWholePage_returnsFullText() throws IOException {
        String text = PdfSelectionExtractor.extractText(document, 0, 0, 0, 612, 792)
                .replaceAll("\\s+", " ")
                .trim();

        assertEquals("hello world", text.toLowerCase());
    }

    @Test
    public void extractText_boxOutsideText_returnsEmpty() throws IOException {
        String text = PdfSelectionExtractor.extractText(document, 0, 0, 0, 10, 10);
        assertTrue(text.isEmpty());
    }
}