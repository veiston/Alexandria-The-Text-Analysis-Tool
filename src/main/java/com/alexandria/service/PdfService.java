package com.alexandria.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import com.itextpdf.kernel.pdf.canvas.parser.listener.SimpleTextExtractionStrategy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/* A proper implementation of PDF text extraction using iText.
   extractText() returns all pages as one string.
   extractPages() returns a map of page number to page text. */

public class PdfService {

	public String extractText(File file) throws IOException {
		if (file == null || !file.exists()) throw new FileNotFoundException("PDF not found: " + file);

		var fullText = new StringBuilder();
		try (var reader = new PdfReader(file);
			 var pdf = new PdfDocument(reader)) {

			for (int p = 1; p <= pdf.getNumberOfPages(); p++) {
				String page = PdfTextExtractor.getTextFromPage(pdf.getPage(p), new SimpleTextExtractionStrategy());
				if (page != null && !page.isBlank()) fullText.append(page).append("\n\n");
			}
		}
		return fullText.toString().strip();
	}

	public Map<Integer, String> extractPages(File file) throws IOException {
		if (file == null || !file.exists()) throw new FileNotFoundException("PDF not found: " + file);

		var pages = new LinkedHashMap<Integer, String>();
		try (var reader = new PdfReader(file);
			 var pdf = new PdfDocument(reader)) {

			for (int p = 1; p <= pdf.getNumberOfPages(); p++) {
				String page = PdfTextExtractor.getTextFromPage(pdf.getPage(p), new SimpleTextExtractionStrategy());
				pages.put(p, page != null ? page : "");
			}
		}
		return Collections.unmodifiableMap(pages);
	}
}
