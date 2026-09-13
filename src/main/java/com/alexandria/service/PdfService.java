package com.alexandria.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import com.itextpdf.kernel.pdf.canvas.parser.listener.SimpleTextExtractionStrategy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/* A proper implementation of PDF text extraction using iText.
   extractText() returns all pages as one string.
   extractPages() returns a map of page number to page text. */

public class PdfService {

	public String extractText(File file) throws IOException {
		return extractTextWithPageBoundaries(file).content();
	}

	public Map<Integer, String> extractPages(File file) throws IOException {
		if (file == null || !file.exists()) throw new FileNotFoundException("PDF not found: " + file);

		var pages = new LinkedHashMap<Integer, String>();
		try (var reader = new PdfReader(file);
			 var pdf = new PdfDocument(reader)) {

			for (int p = 1; p <= pdf.getNumberOfPages(); p++) {
				String page = PdfTextExtractor.getTextFromPage(pdf.getPage(p), new SimpleTextExtractionStrategy());
				if (page != null) {
					pages.put(p, page);
				} else {
					pages.put(p, "");
				}
			}
		}
		return Collections.unmodifiableMap(pages);
	}

	public PagedText extractTextWithPageBoundaries(File file) throws IOException {
		if (file == null || !file.exists()) throw new FileNotFoundException("PDF not found: " + file);

		var fullText = new StringBuilder();
		var offsets = new ArrayList<Integer>();
		var pageNumbers = new ArrayList<Integer>();

		try (var reader = new PdfReader(file);
			 var pdf = new PdfDocument(reader)) {

			for (int p = 1; p <= pdf.getNumberOfPages(); p++) {
				String page = PdfTextExtractor.getTextFromPage(pdf.getPage(p), new SimpleTextExtractionStrategy());
				if (page != null && !page.isBlank()) {
					offsets.add(fullText.length());
					pageNumbers.add(p);
					fullText.append(page).append("\n\n");
				}
			}
		}
		return new PagedText(fullText.toString().strip(), Collections.unmodifiableList(offsets), Collections.unmodifiableList(pageNumbers));
	}

	public record PagedText(String content, List<Integer> pageOffsets, List<Integer> pageNumbers) {

		public PagedText(String content, List<Integer> pageOffsets) {
			this(content, pageOffsets, Collections.emptyList());
		}

		public int pageAt(int charOffset) {
			if (pageOffsets.isEmpty()) return -1;

			int idx = Collections.binarySearch(pageOffsets, charOffset);
			int index;
			if (idx >= 0) {
				index = idx;
			} else {
				index = Math.max(0, -idx - 2);
			}

			if (pageNumbers != null && !pageNumbers.isEmpty() && index < pageNumbers.size()) {
				return pageNumbers.get(index);
			}
			return index + 1;
		}
	}
}
