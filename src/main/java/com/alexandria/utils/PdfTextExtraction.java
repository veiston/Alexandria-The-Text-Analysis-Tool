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
                text.append(PdfTextExtractor.getTextFromPage(pdfDoc.getPage(i)));
                text.append("\n\n");
            }
        }

        return text.toString();
    }
}
