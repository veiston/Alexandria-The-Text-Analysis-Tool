package com.alexandria.controller;

import com.alexandria.model.Text;
import com.alexandria.service.analysis.SearchMatch;
import com.alexandria.service.analysis.SearchServiceINT;
import com.alexandria.service.analysis.SearchSettings;
import com.alexandria.service.analysis.TermAnalysisResult;
import com.alexandria.service.analysis.TermAnalysisServiceINT;
import com.alexandria.service.analysis.TextAnalysisResult;
import com.alexandria.service.analysis.TextAnalysisServiceINT;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class AnalyseControllerTest extends UnitTestBase {

        private SearchServiceINT searchService;
        private TermAnalysisServiceINT termAnalysisService;
        private TextAnalysisServiceINT textAnalysisService;
        private AnalyseController controller;

        @Before
        public void setUp() {
                searchService = mock(SearchServiceINT.class);
                termAnalysisService = mock(TermAnalysisServiceINT.class);
                textAnalysisService = mock(TextAnalysisServiceINT.class);

                controller = new AnalyseController(
                                searchService,
                                termAnalysisService,
                                textAnalysisService);
        }

        // ---------------------------------------------------------
        // openText()
        // ---------------------------------------------------------

        @Test
        public void openText_nullText_returnsError() {
                AnalyseController.TextAnalysisOutcome result = controller.openText(null, List.of());

                assertFalse(result.success());
                assertEquals(
                                "No text was supplied.",
                                result.message());
                assertNull(result.result());

                verifyNoInteractions(textAnalysisService);
        }

        @Test
        public void openText_validText_callsTextAnalysisService() {
                Text text = mock(Text.class);

                when(text.getContent())
                                .thenReturn("This is some test content.");

                when(text.getUserId())
                                .thenReturn(null);

                when(text.getId())
                                .thenReturn(1);

                TextAnalysisResult analysisResult = mock(TextAnalysisResult.class);

                when(textAnalysisService.analyzeText(
                                eq("This is some test content."),
                                eq(List.of(0, 10))))
                                .thenReturn(analysisResult);

                AnalyseController.TextAnalysisOutcome result = controller.openText(
                                text,
                                List.of(0, 10));

                assertTrue(result.success());
                assertNull(result.message());
                assertSame(analysisResult, result.result());

                verify(textAnalysisService).analyzeText(
                                "This is some test content.",
                                List.of(0, 10));
        }

        @Test
        public void openText_nullPageOffsets_usesEmptyList() {
                Text text = mock(Text.class);

                when(text.getContent())
                                .thenReturn("Test content.");

                when(text.getUserId())
                                .thenReturn(null);

                when(text.getId())
                                .thenReturn(1);

                TextAnalysisResult analysisResult = mock(TextAnalysisResult.class);

                when(textAnalysisService.analyzeText(
                                eq("Test content."),
                                eq(List.of())))
                                .thenReturn(analysisResult);

                AnalyseController.TextAnalysisOutcome result = controller.openText(text, null);

                assertTrue(result.success());
                assertSame(analysisResult, result.result());

                verify(textAnalysisService).analyzeText(
                                "Test content.",
                                List.of());
        }

        @Test
        public void openText_analysisServiceThrows_returnsError() {
                Text text = mock(Text.class);

                when(text.getContent())
                                .thenReturn("Test content.");

                when(text.getUserId())
                                .thenReturn(null);

                when(text.getId())
                                .thenReturn(1);

                when(textAnalysisService.analyzeText(
                                any(String.class),
                                any(List.class)))
                                .thenThrow(new RuntimeException("Analysis failed"));

                AnalyseController.TextAnalysisOutcome result = controller.openText(text, List.of());

                assertFalse(result.success());
                assertEquals(
                                "Could not process text analysis: Analysis failed",
                                result.message());
                assertNull(result.result());
        }

        // ---------------------------------------------------------
        // search()
        // ---------------------------------------------------------

        @Test
        public void search_withoutOpenText_returnsError() {
                AnalyseController.SearchOutcome result = controller.search("hello");

                assertFalse(result.success());
                assertEquals(
                                "No text is currently open.",
                                result.message());

                assertTrue(result.matches().isEmpty());
                assertNull(result.termAnalysis());

                verifyNoInteractions(searchService);
                verifyNoInteractions(termAnalysisService);
        }

        @Test
        public void search_nullTerm_returnsError() {
                openTestText();

                AnalyseController.SearchOutcome result = controller.search(null);

                assertFalse(result.success());
                assertEquals(
                                "Search term cannot be empty.",
                                result.message());

                verifyNoInteractions(searchService);
                verifyNoInteractions(termAnalysisService);
        }

        @Test
        public void search_blankTerm_returnsError() {
                openTestText();

                AnalyseController.SearchOutcome result = controller.search("   ");

                assertFalse(result.success());
                assertEquals(
                                "Search term cannot be empty.",
                                result.message());

                verifyNoInteractions(searchService);
                verifyNoInteractions(termAnalysisService);
        }

        @Test
        public void search_validTerm_returnsMatchesAndTermAnalysis() {
                openTestText();

                SearchMatch match1 = mock(SearchMatch.class);
                SearchMatch match2 = mock(SearchMatch.class);

                List<SearchMatch> matches = List.of(match1, match2);

                TermAnalysisResult termResult = mock(TermAnalysisResult.class);

                when(searchService.search(
                                eq("Hello world"),
                                eq("hello"),
                                any(SearchSettings.class),
                                eq(List.of(0))))
                                .thenReturn(matches);

                when(termAnalysisService.analyzeTerm(
                                eq("Hello world"),
                                eq("hello")))
                                .thenReturn(termResult);

                AnalyseController.SearchOutcome result = controller.search("hello");

                assertTrue(result.success());
                assertNull(result.message());
                assertEquals(matches, result.matches());
                assertSame(termResult, result.termAnalysis());

                verify(searchService).search(
                                eq("Hello world"),
                                eq("hello"),
                                any(SearchSettings.class),
                                eq(List.of(0)));

                verify(termAnalysisService).analyzeTerm(
                                "Hello world",
                                "hello");
        }

        @Test
        public void search_withOptions_passesSearchSettings() {
                openTestText();

                TermAnalysisResult termResult = mock(TermAnalysisResult.class);

                when(searchService.search(
                                any(String.class),
                                any(String.class),
                                any(SearchSettings.class),
                                any(List.class)))
                                .thenReturn(List.of());

                when(termAnalysisService.analyzeTerm(
                                any(String.class),
                                any(String.class)))
                                .thenReturn(termResult);

                AnalyseController.SearchOutcome result = controller.search(
                                "hello",
                                true,
                                true,
                                true);

                assertTrue(result.success());

                verify(searchService).search(
                                eq("Hello world"),
                                eq("hello"),
                                argThat(settings -> settings.caseSensitive()
                                                && settings.fuzzy()
                                                && settings.wholeWordsOnly()),
                                eq(List.of(0)));
        }

        @Test
        public void search_serviceThrows_returnsError() {
                openTestText();

                when(searchService.search(
                                any(String.class),
                                any(String.class),
                                any(SearchSettings.class),
                                any(List.class)))
                                .thenThrow(new RuntimeException("Search failed"));

                AnalyseController.SearchOutcome result = controller.search("hello");

                assertFalse(result.success());
                assertEquals(
                                "Could not complete search: Search failed",
                                result.message());

                assertTrue(result.matches().isEmpty());
                assertNull(result.termAnalysis());
        }

        // ---------------------------------------------------------
        // nextMatch()
        // ---------------------------------------------------------

        @Test
        public void nextMatch_withoutSearch_returnsNull() {
                assertNull(controller.nextMatch());
        }

        @Test
        public void nextMatch_movesThroughMatches() {
                openTestText();

                SearchMatch match1 = mock(SearchMatch.class);
                SearchMatch match2 = mock(SearchMatch.class);
                SearchMatch match3 = mock(SearchMatch.class);

                List<SearchMatch> matches = List.of(match1, match2, match3);

                prepareSearch(matches);

                controller.search("hello");

                assertSame(match2, controller.nextMatch());
                assertSame(match3, controller.nextMatch());
                assertSame(match1, controller.nextMatch());
                assertSame(match2, controller.nextMatch());
        }

        // ---------------------------------------------------------
        // previousMatch()
        // ---------------------------------------------------------

        @Test
        public void previousMatch_withoutSearch_returnsNull() {
                assertNull(controller.previousMatch());
        }

        @Test
        public void previousMatch_movesBackThroughMatches() {
                openTestText();

                SearchMatch match1 = mock(SearchMatch.class);
                SearchMatch match2 = mock(SearchMatch.class);
                SearchMatch match3 = mock(SearchMatch.class);

                List<SearchMatch> matches = List.of(match1, match2, match3);

                prepareSearch(matches);

                controller.search("hello");

                assertSame(match3, controller.previousMatch());
                assertSame(match2, controller.previousMatch());
                assertSame(match1, controller.previousMatch());
                assertSame(match3, controller.previousMatch());
        }

        // ---------------------------------------------------------
        // Search state
        // ---------------------------------------------------------

        @Test
        public void search_success_startsAtFirstMatch() {
                openTestText();

                SearchMatch match1 = mock(SearchMatch.class);
                SearchMatch match2 = mock(SearchMatch.class);

                prepareSearch(List.of(match1, match2));

                controller.search("hello");

                assertSame(match2, controller.nextMatch());
        }

        @Test
        public void search_withNoMatches_nextAndPreviousReturnNull() {
                openTestText();

                prepareSearch(List.of());

                AnalyseController.SearchOutcome result = controller.search("hello");

                assertTrue(result.success());
                assertTrue(result.matches().isEmpty());

                assertNull(controller.nextMatch());
                assertNull(controller.previousMatch());
        }

        @Test
        public void failedSearch_clearsPreviousSearchState() {
                openTestText();

                SearchMatch match1 = mock(SearchMatch.class);

                prepareSearch(List.of(match1));

                controller.search("hello");

                when(searchService.search(
                                any(String.class),
                                eq("bad"),
                                any(SearchSettings.class),
                                any(List.class)))
                                .thenThrow(new RuntimeException("Search failed"));

                AnalyseController.SearchOutcome result = controller.search("bad");

                assertFalse(result.success());

                assertNull(controller.nextMatch());
                assertNull(controller.previousMatch());
        }

        // ---------------------------------------------------------
        // Helpers
        // ---------------------------------------------------------

        private void openTestText() {
                Text text = mock(Text.class);

                when(text.getContent())
                                .thenReturn("Hello world");

                when(text.getUserId())
                                .thenReturn(null);

                when(text.getId())
                                .thenReturn(1);

                TextAnalysisResult analysisResult = mock(TextAnalysisResult.class);

                when(textAnalysisService.analyzeText(
                                any(String.class),
                                any(List.class)))
                                .thenReturn(analysisResult);

                controller.openText(text, List.of(0));
        }

        private void prepareSearch(List<SearchMatch> matches) {
                TermAnalysisResult termResult = mock(TermAnalysisResult.class);

                when(searchService.search(
                                any(String.class),
                                any(String.class),
                                any(SearchSettings.class),
                                any(List.class)))
                                .thenReturn(matches);

                when(termAnalysisService.analyzeTerm(
                                any(String.class),
                                any(String.class)))
                                .thenReturn(termResult);
        }
}
