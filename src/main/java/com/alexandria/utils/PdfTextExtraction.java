package com.alexandria.utils;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

import java.io.IOException;

public class PdfTextExtraction {

    public static String extractText(String filePath) throws IOException {
        StringBuilder text = new StringBuilder();

        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(filePath))) {

            int pageCount = pdfDoc.getNumberOfPages();

            for (int i = 1; i <= pageCount; i++) {

                String pageText = PdfTextExtractor.getTextFromPage(
                        pdfDoc.getPage(i));

                // Ignore completely empty pages
                if (pageText.trim().isEmpty()) {
                    continue;
                }

                // Normalize whitespace/newlines returned by iText.
                String normalizedText = pageText
                        .trim()
                        .replaceAll("\\r\\n|\\r|\\n", "\n\n");

                // Separate pages with two newlines.
                if (text.length() > 0) {
                    text.append("\n\n");
                }

                text.append(normalizedText);
            }
        }

        return text.toString();
    }
}
