package com.alexandria.service;

import com.alexandria.model.TermComparisonResult;
import com.alexandria.model.TextComparisonResult;
import com.alexandria.service.analysis.SearchMatch;
import com.alexandria.service.analysis.SearchSettings;
import com.alexandria.service.analysis.TermAnalysisResult;
import com.alexandria.service.analysis.TextAnalysisResult;
import com.alexandria.service.analysis.TextFragment;
import com.alexandria.service.analysis.WordFrequency;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TextAnalysisServiceTest {

    private TextAnalysisService service;

    @Before
    public void setUp() {
        service = new TextAnalysisService();
    }

    @Test
    public void testAnalyzeText() {
        String text = "This is a test. This is only a test.";
        TextAnalysisResult result = service.analyzeText(text);

        assertNotNull(result);
        assertEquals(9, result.totalWords()); // This is a test This is only a test
        assertEquals(2, result.totalSentences());
        assertEquals(1, result.totalParagraphs());
        assertTrue(result.frequentWords().size() > 0);
    }

    @Test
    public void testAnalyzeTerm() {
        String text = "Test word in a test sentence. Another test word here.";
        TermAnalysisResult result = service.analyzeTerm(text, "test");

        assertNotNull(result);
        assertEquals("test", result.term());
        assertEquals(3, result.totalOccurrences());
        assertEquals(2, result.sentenceCount());
        assertEquals(1, result.paragraphCount());
    }

    @Test
    public void testSearch() {
        String text = "Look for the needle in the haystack.";
        List<SearchMatch> matches = service.search(text, "needle", new SearchSettings(false, false, false));

        assertNotNull(matches);
        assertEquals(1, matches.size());
        assertEquals("needle", matches.get(0).text());
    }

    @Test
    public void testCompareTexts() {
        Map<Integer, String> texts = new HashMap<>();
        texts.put(1, "The quick brown fox jumps.");
        texts.put(2, "The lazy dog sleeps.");

        TextComparisonResult result = service.compareTexts(texts, 10);
        assertNotNull(result);
        assertEquals(2, result.textIds().size());
    }

    @Test
    public void testCompareTerm() {
        Map<Integer, String> texts = new HashMap<>();
        texts.put(1, "Fox fox fox.");
        texts.put(2, "No fox here.");

        TermComparisonResult result = service.compareTerm(texts, null, "fox");
        assertNotNull(result);
        assertEquals("fox", result.term());
        assertEquals(2, result.occurrencesPerText().size());
    }
}
