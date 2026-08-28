package com.alexandria;

// DIRIN:Bowel sort,radix,sort,quicksort

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
        String filename = "Meditations.pdf";
        File file = new File(filename);
        Dictionary<Integer, String> extractedPages = new Hashtable<>(); // Store pages
        long time = System.currentTimeMillis();

        if (!file.exists()) {
            System.err.println("Error: Neither 'Meditations.pdf' nor 'sample.pdf' was found.");
            return;
        }

        // System.out.printf("Reading: %s%n", file.getAbsolutePath());

        try (PdfReader reader = new PdfReader(file);
                PdfDocument pdf = new PdfDocument(reader)) {

            for (int pageNum = 1; pageNum <= pdf.getNumberOfPages(); pageNum++) {
                String text = PdfTextExtractor.getTextFromPage(pdf.getPage(pageNum),
                        new SimpleTextExtractionStrategy());
                // System.out.printf("=== Page %d ===%n%s%n", p, text);
                extractedPages.put(pageNum, text);
            }
            System.out.println(extractedPages);
            System.out.println();
            // for (int pageNum=1; pageNum <=)

            time = System.currentTimeMillis() - time;
            System.out.println("Took " + time + " ms");

        } catch (IOException e) {
            System.err.printf("Failed to read PDF: %s%n", e.getMessage());
        }
    }
}