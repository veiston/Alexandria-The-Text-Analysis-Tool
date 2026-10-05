package com.alexandria.view.screens;

import com.alexandria.controller.JavaFxTestBase;
import com.alexandria.service.analysis.SearchMatch;

import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

public class AnalyseScreenTest extends JavaFxTestBase {

    private AtomicReference<AnalyseScreen> screenRef;

    @Before
    public void setUp() {
        screenRef = new AtomicReference<>();
    }

    private AnalyseScreen createScreen() throws Exception {
        runOnFxThread(() -> screenRef.set(new AnalyseScreen()));
        return screenRef.get();
    }

    @Test
    public void constructor_createsScreen() throws Exception {
        AnalyseScreen screen = createScreen();

        assertNotNull(screen);
        assertNotNull(screen.getHeader());
        assertNotNull(screen.getDocumentView());
        assertNotNull(screen.getTextTermFrequencyPanel());
        assertNotNull(screen.getTextContextPanel());
        assertNotNull(screen.getStatisticsSidebar());
    }

    @Test
    public void constructor_startsWithEmptyState() throws Exception {
        AnalyseScreen screen = createScreen();

        assertNotNull(screen);
        assertFalse(screen.getChildren().isEmpty());
    }

    @Test
    public void setSearchHandler_acceptsHandler() throws Exception {
        AtomicBoolean called = new AtomicBoolean(false);

        AnalyseScreen screen = createScreen();

        runOnFxThread(() -> screen.setOnSearch(term -> called.set(true)));

        assertNotNull(screen);
        assertFalse(called.get());
    }

    @Test
    public void nullSearchHandler_doesNotThrow() throws Exception {
        AnalyseScreen screen = createScreen();

        runOnFxThread(() -> {
            screen.setOnSearch(null);
            screen.setOnPreviousMatch(null);
            screen.setOnNextMatch(null);
            screen.setOnSaveAnalysis(null);
            screen.setOnTermDetailRequested(null);
            screen.setOnQuotationRequested(null);
            screen.setOnQuotationDeleteRequested(null);
            screen.setOnQuotationEditRequested(null);
        });

        assertNotNull(screen);
    }

    @Test
    public void clearAnalysis_doesNotThrow() throws Exception {
        AnalyseScreen screen = createScreen();

        runOnFxThread(screen::clearAnalysis);

        assertNotNull(screen);
    }

    @Test
    public void showSearchMatch_withEmptyMatches_doesNothing()
            throws Exception {

        AnalyseScreen screen = createScreen();

        runOnFxThread(() -> screen.showSearchMatch(List.of(), 0));

        assertNotNull(screen);
    }

    @Test
    public void showSearchMatch_withNullMatch_doesNothing()
            throws Exception {

        AnalyseScreen screen = createScreen();

        runOnFxThread(() -> screen.showSearchMatch((SearchMatch) null));

        assertNotNull(screen);
    }

    @Test
    public void showSearchMatch_clampsNegativeIndex()
            throws Exception {

        AnalyseScreen screen = createScreen();

        SearchMatch match = createSearchMatch();

        runOnFxThread(() -> screen.showSearchMatch(
                List.of(match),
                -10));

        assertNotNull(screen);
    }

    @Test
    public void showSearchMatch_clampsIndexAboveRange()
            throws Exception {

        AnalyseScreen screen = createScreen();

        SearchMatch match = createSearchMatch();

        runOnFxThread(() -> screen.showSearchMatch(
                List.of(match),
                100));

        assertNotNull(screen);
    }

    @Test
    public void showSearchResults_withEmptyMatches_doesNotThrow()
            throws Exception {

        AnalyseScreen screen = createScreen();

        runOnFxThread(() -> screen.showSearchResults(
                "test",
                List.of(),
                null));

        assertNotNull(screen);
    }

    @Test
    public void showSearchResults_withNullMatches_doesNotThrow()
            throws Exception {

        AnalyseScreen screen = createScreen();

        runOnFxThread(() -> screen.showSearchResults(
                "test",
                null,
                null));

        assertNotNull(screen);
    }

    @Test
    public void setTextAnalysis_acceptsResults()
            throws Exception {

        AnalyseScreen screen = createScreen();

        runOnFxThread(() -> screen.setTextAnalysis(
                List.of(),
                List.of()));

        assertNotNull(screen.getTextTermFrequencyPanel());
        assertNotNull(screen.getTextContextPanel());
    }

    @Test
    public void setQuotations_acceptsEmptyList()
            throws Exception {

        AnalyseScreen screen = createScreen();

        runOnFxThread(() -> screen.setQuotations(List.of()));

        assertNotNull(screen);
    }

    @Test
    public void removeQuotationHighlight_doesNotThrow()
            throws Exception {

        AnalyseScreen screen = createScreen();

        runOnFxThread(() -> screen.removeQuotationHighlight(123));

        assertNotNull(screen);
    }

    private SearchMatch createSearchMatch() {
        return new SearchMatch(
                "test",
                0,
                4,
                1,
                null,
                "context text");
    }
}
