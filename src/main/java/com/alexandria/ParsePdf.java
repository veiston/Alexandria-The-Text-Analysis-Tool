package com.alexandria;

// DIRIN:Bowel sort,radix,sort,quicksort
// Probably Radix sort or quicksort. Copy c++ implementation?

import java.io.File;
import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import com.itextpdf.kernel.pdf.canvas.parser.listener.SimpleTextExtractionStrategy;

public class ParsePdf {
    public static void main(String[] args) {
        // TODO: Implement a file select system + auto directory scanning for .txt and .pdf
        String filename = "Meditations.pdf";
        File file = new File(filename);
        Dictionary<Integer, String> extractedPages = new Hashtable<>(); // Store pages
        long time = System.currentTimeMillis();

        if (!file.exists()) {
            System.err.printf("Error: '%s' was not found :'( '.%n", filename);
            return;
        }

        try (PdfReader reader = new PdfReader(file);
                PdfDocument pdf = new PdfDocument(reader)) {

            for (int pageNum = 1; pageNum <= pdf.getNumberOfPages(); pageNum++) {
                String text = PdfTextExtractor.getTextFromPage(pdf.getPage(pageNum),
                        new SimpleTextExtractionStrategy());
                // System.out.printf("=== Page %d ===%n%s%n", p, text); // Print clear print page breaks
                extractedPages.put(pageNum, text);
            }
            System.out.println(extractedPages);
            System.out.println();
            // for (int pageNum=1; pageNum <=)

            // Performance measurement
            time = System.currentTimeMillis() - time;
            System.out.println("Took " + time + " ms");

        } catch (IOException e) {
            System.err.printf("Failed to read PDF: %s%n", e.getMessage());
        }
    }
}