package com.alexandria.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import com.alexandria.service.analysis.SearchMatch;
import com.alexandria.service.analysis.SearchSettings;
import com.alexandria.service.analysis.TermAnalysisResult;
import com.alexandria.service.analysis.TermComparisonResult;
import com.alexandria.service.analysis.TextAnalysisResult;
import com.alexandria.service.analysis.TextComparisonResult;

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
        List<SearchMatch> matches = searchService.search(
                text, "needle", new SearchSettings(false, false, false), List.of());

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
    public void testCompareTextsCountsAllWords() {
        Map<Integer, String> texts = new HashMap<>();
        texts.put(1, "marcus marcus marcus romulus remus nero augustus peach");
        texts.put(2, "first first first second second third third fourth fourth fifth fifth marcus");

        TextComparisonResult result = textComparisonService.compareTexts(texts, 10);
        assertNotNull(result);

        TextComparisonResult.TextComparisonRow marcusRow = result.commonWords().stream()
                .filter(row -> row.word().equals("marcus"))
                .findFirst()
                .orElse(null);

        assertNotNull(marcusRow);
        assertEquals(Integer.valueOf(3), marcusRow.countsByTextId().get(1));
        assertEquals(Integer.valueOf(1), marcusRow.countsByTextId().get(2));
        assertTrue(marcusRow.relativeFreqByTextId().get(2) > 0.0);
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
    public void testCompareTermWithIgnoreStopWords() {
        Map<Integer, String> texts = new HashMap<>();
        texts.put(1, "The Romulus is fast.");
        texts.put(2, "The Remus is small.");

        TermComparisonResult withStopWords = termComparisonService.compareTerm(texts, null, "the", false);
        assertNotNull(withStopWords);
        assertEquals(2, withStopWords.occurrencesPerText().size());

        TermComparisonResult withoutStopWords = termComparisonService.compareTerm(texts, null, "the", true);
        assertNotNull(withoutStopWords);
        assertEquals(0, withoutStopWords.occurrencesPerText().size());
    }

    @Test
    public void testSearchPreservesActualMatchedCasing() {
        String text = "Quick BROWN fox jumps over the lazy Dog.";
        List<SearchMatch> matches = searchService.search(
                text, "BROWN", new SearchSettings(false, false, false), List.of());
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

    @Test
    public void testSearchPopulatesPageAndParagraph() {
        String text = "First paragraph content.\n\nSecond paragraph has the needle in it.";
        List<Integer> pageOffsets = List.of(0, 26);
        List<SearchMatch> matches = searchService.search(text, "needle", new SearchSettings(false, false, false), pageOffsets);

        assertNotNull(matches);
        assertEquals(1, matches.size());
        SearchMatch match = matches.get(0);
        assertEquals("needle", match.text());
        assertEquals(Integer.valueOf(2), match.page());
        assertEquals(Integer.valueOf(2), match.paragraph());
    }

    @Test
    public void testFuzzySearchWithWholeWordsSettings() {
        String text = "We walked along the riverside.";
        
        // When whole words OFF: "riverside" should match "river"
        List<SearchMatch> subMatches = searchService.search(text, "river", new SearchSettings(false, true, false), null);
        assertNotNull(subMatches);
        assertEquals(1, subMatches.size());
        assertEquals("river", subMatches.get(0).text());

        // Whole words ON: "riverside" should NOT match "river"
        List<SearchMatch> wholeMatches = searchService.search(text, "river", new SearchSettings(false, true, true), null);
        assertNotNull(wholeMatches);
        assertEquals(0, wholeMatches.size());
    }

    @Test
    public void testFuzzySearchWithCaseSensitivity() {
        String text = "River and river flowing.";

        List<SearchMatch> insensitive = searchService.search(text, "river", new SearchSettings(false, true, false), null);
        assertNotNull(insensitive);
        assertEquals(2, insensitive.size());
    }

    @Test
    public void testSearchWithIgnoreStopWords() {
        String text = "The Roman empire was the grandest.";

        List<SearchMatch> withStopWords = searchService.search(text, "the", new SearchSettings(false, false, false, false), null);
        assertNotNull(withStopWords);
        assertEquals(2, withStopWords.size());

        List<SearchMatch> withoutStopWords = searchService.search(text, "the", new SearchSettings(false, false, false, true), null);
        assertNotNull(withoutStopWords);
        assertEquals(0, withoutStopWords.size());
    }

    @Test
    public void testFuzzySearchWithIgnoreStopWords() {
        String text = "He is on the right path.";

        List<SearchMatch> withoutStopWords = searchService.search(text, "the", new SearchSettings(false, true, true, true), null);
        assertNotNull(withoutStopWords);
        assertEquals(0, withoutStopWords.size());
    }

    @Test(timeout = 5000)
    public void testAnalyzeLargeTextPerformance() {
        String text = "text ".repeat(100_000);

        long startedAt = System.nanoTime();
        TextAnalysisResult result = textService.analyzeText(text);
        long durationMillis = (System.nanoTime() - startedAt) / 1_000_000;

        assertEquals(100_000, result.totalWords());
        System.out.println("100 000 word analysis completed in " + durationMillis + " ms");
    }

    @Test(timeout = 5000)
    public void testSearchLargeTextPerformance() {
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < 100_000; i++) {
            if (i % 1_000 == 0) {
				text.append("target ");
			} else {
				text.append("word ");
			}
        }

        long startedAt = System.nanoTime();
        List<SearchMatch> matches = searchService.search(text.toString(), "target", SearchSettings.defaults(), List.of());
        long durationMillis = (System.nanoTime() - startedAt) / 1_000_000;

        assertEquals(100, matches.size());
        System.out.println("100 000 word search completed in " + durationMillis + " ms");
    }
}
