package com.alexandria.utils;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PdfTextExtractionTest {

    @Test
    public void extractTextReadsTextFromSinglePagePdf()
            throws Exception {

        File pdf = File.createTempFile(
                "test-document",
                ".pdf");

        try {
            createPdf(
                    pdf,
                    "Hello Alexandria");

            String text = PdfTextExtraction.extractText(
                    pdf.getAbsolutePath());

            assertTrue(
                    text.contains("Hello Alexandria"));

        } finally {
            pdf.delete();
        }
    }

    @Test
    public void extractTextSeparatesMultiplePages()
            throws Exception {

        File pdf = File.createTempFile(
                "multi-page",
                ".pdf");

        try {
            createMultiPagePdf(pdf);

            String text = PdfTextExtraction.extractText(
                    pdf.getAbsolutePath());

            assertTrue(text.contains("Page One"));
            assertTrue(text.contains("Page Two"));

            assertTrue(
                    text.contains("Page One\n\n"));

        } finally {
            pdf.delete();
        }
    }

    @Test
    public void extractTextReturnsEmptyTextForEmptyPdf()
            throws Exception {

        File pdf = File.createTempFile(
                "empty",
                ".pdf");

        try {
            createEmptyPdf(pdf);

            String text = PdfTextExtraction.extractText(
                    pdf.getAbsolutePath());

            assertEquals("", text);

        } finally {
            pdf.delete();
        }
    }

    @Test(expected = Exception.class)
    public void extractTextFailsForMissingFile()
            throws Exception {

        PdfTextExtraction.extractText(
                "does-not-exist.pdf");
    }

    private void createPdf(
            File file,
            String text) throws Exception {

        try (PdfWriter writer = new PdfWriter(file.getAbsolutePath());
                PdfDocument pdf = new PdfDocument(writer);
                Document document = new Document(pdf)) {

            document.add(
                    new Paragraph(text));
        }
    }

    private void createMultiPagePdf(
            File file) throws Exception {

        try (PdfWriter writer = new PdfWriter(file.getAbsolutePath());
                PdfDocument pdf = new PdfDocument(writer);
                Document document = new Document(pdf)) {

            document.add(
                    new Paragraph("Page One"));

            document.add(
                    new Paragraph("Page One content"));

            document.add(
                    new com.itextpdf.layout.element.AreaBreak());

            document.add(
                    new Paragraph("Page Two"));

            document.add(
                    new Paragraph("Page Two content"));
        }
    }

    private void createEmptyPdf(
            File file) throws Exception {

        try (PdfWriter writer = new PdfWriter(file.getAbsolutePath());
                PdfDocument pdf = new PdfDocument(writer);
                Document document = new Document(pdf)) {
        }
    }

}