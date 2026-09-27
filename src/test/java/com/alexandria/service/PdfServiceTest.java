package com.alexandria.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import com.alexandria.service.analysis.SearchMatch;

/**
 * PDF Service Test.
 *
 * Verifies text extraction and page extraction from PDF documents. Also Bitap fuzzysearch
 */
public class PdfServiceTest {

	private PdfService pdfService;

	@Before
	public void setup() {
		pdfService = new PdfService();
	}

	@Test
	public void testExtractTextFromExistingPdf() throws IOException {
		File pdfFile = new File("Meditations.pdf");
		if (pdfFile.exists()) {
			String text = pdfService.extractText(pdfFile);
			assertNotNull(text);
			assertFalse("Extracted text should not be empty", text.isBlank());
			assertTrue("Extracted text should contain words", text.length() > 100);

			Map<Integer, String> pages = pdfService.extractPages(pdfFile);
			assertNotNull(pages);
			assertFalse("Pages should not be empty", pages.isEmpty());
		}
	}

	@Test
	public void testFuzzySearchOnMeditations() throws IOException {
		File pdfFile = new File("Meditations.pdf");
		if (pdfFile.exists()) {
			String text = pdfService.extractText(pdfFile);
			FuzzySearchService fuzzyService = new FuzzySearchService();
			// Test with exact and typo terms
			List<SearchMatch> exactMatches = fuzzyService.findWithFuzzy(text, "Marcus");
			assertNotNull(exactMatches);
			assertFalse("Should find matches for Marcus in Meditations", exactMatches.isEmpty());
			System.out.println("Found " + exactMatches.size() + " matches for 'Marcus'");
			exactMatches.stream().limit(3).forEach(m -> System.out.println("  -> [" + m.matchStart() + ".." + m.matchEnd() + "]: \"" + m.text() + "\""));

			// Test fuzzy search with the term Marcsu and Grek
			List<SearchMatch> typoMatches = fuzzyService.findWithFuzzy(text, "Marcsu");
			assertNotNull(typoMatches);
			System.out.println("Found " + typoMatches.size() + " fuzzy matches for typo 'Marcsu'");
			typoMatches.stream().limit(3).forEach(m -> System.out.println("  -> [" + m.matchStart() + ".." + m.matchEnd() + "]: \"" + m.text() + "\""));

			typoMatches = fuzzyService.findWithFuzzy(text, "Grek");
			assertNotNull(typoMatches);
			System.out.println("Found " + typoMatches.size() + " fuzzy matches for typo 'Grek'");
			typoMatches.stream().limit(3).forEach(m -> System.out.println("  -> [" + m.matchStart() + ".." + m.matchEnd() + "]: \"" + m.text() + "\""));
		}
	}

	@Test(expected = FileNotFoundException.class)
	public void testExtractTextFromNonExistentPdf() throws IOException {
		pdfService.extractText(new File("non_existent_file.pdf"));
	}

	@Test(expected = FileNotFoundException.class)
	public void testExtractPagesFromNullFile() throws IOException {
		pdfService.extractPages(null);
	}
}
