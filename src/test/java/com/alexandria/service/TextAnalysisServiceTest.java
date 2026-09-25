package com.alexandria.service;

import com.alexandria.service.analysis.TermComparisonResult;
import com.alexandria.service.analysis.TextComparisonResult;
import com.alexandria.service.analysis.SearchMatch;
import com.alexandria.service.analysis.SearchSettings;
import com.alexandria.service.analysis.TermAnalysisResult;
import com.alexandria.service.analysis.TextAnalysisResult;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TextAnalysisServiceTest {

    private TextAnalysisService textService;
    private TermAnalysisService termService;
    private SearchService searchService;
    private TextComparisonService textComparisonService;
    private TermComparisonService termComparisonService;

    @Before
    public void setUp() {
        textService = new TextAnalysisService();
        termService = new TermAnalysisService();
        searchService = new SearchService();
        textComparisonService = new TextComparisonService();
        termComparisonService = new TermComparisonService();
    }

    @Test
    public void testAnalyzeText() {
        String text = "This is a test. This is only a test.";
        TextAnalysisResult result = textService.analyzeText(text);

        assertNotNull(result);
        assertEquals(9, result.totalWords()); // This is a test This is only a test
        assertEquals(2, result.totalSentences());
        assertEquals(1, result.totalParagraphs());
        assertTrue(result.frequentWords().size() > 0);
    }

    @Test
    public void testAnalyzeTerm() {
        String text = "Test word in a test sentence. Another test word here. Climate change is real. We must fight climate change.";
        TermAnalysisResult result = termService.analyzeTerm(text, "test");

        assertNotNull(result);
        assertEquals("test", result.term());
        assertEquals(3, result.totalOccurrences());
        assertEquals(2, result.sentenceCount());
        assertEquals(1, result.paragraphCount());
        
        // Test phrase support
        TermAnalysisResult phraseResult = termService.analyzeTerm(text, "climate change");
        assertEquals("climate change", phraseResult.term());
        assertEquals(2, phraseResult.totalOccurrences());
        assertEquals(2, phraseResult.sentenceCount());
    }

    @Test
    public void testSearch() {
        String text = "Look for the needle in the haystack. It is super hard to find.";
        List<SearchMatch> matches = searchService.search(text, "needle", new SearchSettings(false, false, false), null);

        assertNotNull(matches);
        assertEquals(1, matches.size());
        assertEquals("needle", matches.get(0).text());
        assertTrue(matches.get(0).context().contains("Look for the needle in the haystack"));
    }

    @Test
    public void testCompareTexts() {
        Map<Integer, String> texts = new HashMap<>();
        texts.put(1, "The quick brown fox jumps.");
        texts.put(2, "The lazy dog sleeps.");

        TextComparisonResult result = textComparisonService.compareTexts(texts, 10);
        assertNotNull(result);
        assertEquals(2, result.textIds().size());
    }

    @Test
    public void testCompareTerm() {
        Map<Integer, String> texts = new HashMap<>();
        texts.put(1, "Fox fox fox.");
        texts.put(2, "No fox here.");

        TermComparisonResult result = termComparisonService.compareTerm(texts, null, "fox");
        assertNotNull(result);
        assertEquals("fox", result.term());
        assertEquals(2, result.occurrencesPerText().size());
    }

    @Test
    public void testSearchPreservesActualMatchedCasing() {
        String text = "Quick BROWN fox jumps over the lazy Dog.";
        List<SearchMatch> matches = searchService.search(text, "BROWN", new SearchSettings(false, false, false), null);
        assertEquals(1, matches.size());
        assertEquals("BROWN", matches.get(0).text());
    }

    @Test
    public void testAnalyzeTextWithPageOffsets() {
        String text = "Page one content here.\n\nPage two starts here with important terms.";
        List<Integer> pageOffsets = List.of(0, 24);
        TextAnalysisResult result = textService.analyzeText(text, pageOffsets);
        assertNotNull(result);
        assertEquals(2, result.totalParagraphs());
        assertTrue(result.importantFragments().stream().anyMatch(f -> f.page() != null));
    }

    @Test
    public void testAnalyzePhraseWithNeighbors() {
        String text = "Global warming causes problems. Rapid global warming brings destruction.";
        TermAnalysisResult result = termService.analyzeTerm(text, "global warming");
        assertEquals("global warming", result.term());
        assertEquals(2, result.totalOccurrences());
        assertNotNull(result.neighboringWords());
        assertTrue(result.neighboringWords().size() > 0);
    }
}
