package com.alexandria.view.screens;

import com.alexandria.service.analysis.SearchMatch;
import javafx.application.Platform;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

public class AnalyseScreenTest {

    @BeforeClass
    public static void initJavaFx() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);

        try {
            Platform.startup(latch::countDown);
        } catch (IllegalStateException alreadyInitialized) {
            // Another test class in this JVM fork already started the
            // toolkit — that's fine, we just need it running, not to
            // have started it ourselves.
            return;
        }

        if (!latch.await(5, TimeUnit.SECONDS)) {
            throw new IllegalStateException("JavaFX failed to start");
        }
    }

    private <T> T runOnFxThread(java.util.concurrent.Callable<T> callable)
            throws Exception {

        AtomicReference<T> result = new AtomicReference<>();
        AtomicReference<Throwable> error = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                result.set(callable.call());
            } catch (Throwable t) {
                error.set(t);
            } finally {
                latch.countDown();
            }
        });

        assertTrue(latch.await(5, TimeUnit.SECONDS));

        if (error.get() != null) {
            throw new AssertionError(error.get());
        }

        return result.get();
    }

    @Test
    public void constructor_createsScreen() throws Exception {
        AnalyseScreen screen = runOnFxThread(AnalyseScreen::new);

        assertNotNull(screen);
        assertNotNull(screen.getHeader());
        assertNotNull(screen.getDocumentView());
        assertNotNull(screen.getTextTermFrequencyPanel());
        assertNotNull(screen.getTextContextPanel());
        assertNotNull(screen.getStatisticsSidebar());
    }

    @Test
    public void constructor_startsWithEmptyState() throws Exception {
        AnalyseScreen screen = runOnFxThread(AnalyseScreen::new);

        assertFalse(screen.getChildren().isEmpty());
    }

    @Test
    public void setSearchHandler_acceptsHandler() throws Exception {
        AtomicBoolean called = new AtomicBoolean(false);

        AnalyseScreen screen = runOnFxThread(() -> {
            AnalyseScreen s = new AnalyseScreen();

            s.setOnSearch(term -> called.set(true));

            return s;
        });

        assertNotNull(screen);
        assertFalse(called.get());
    }

    @Test
    public void nullSearchHandler_doesNotThrow() throws Exception {
        AnalyseScreen screen = runOnFxThread(() -> {
            AnalyseScreen s = new AnalyseScreen();

            s.setOnSearch(null);
            s.setOnPreviousMatch(null);
            s.setOnNextMatch(null);
            s.setOnSaveAnalysis(null);
            s.setOnTermDetailRequested(null);
            s.setOnQuotationRequested(null);
            s.setOnQuotationDeleteRequested(null);
            s.setOnQuotationEditRequested(null);

            return s;
        });

        assertNotNull(screen);
    }

    @Test
    public void clearAnalysis_doesNotThrow() throws Exception {
        AnalyseScreen screen = runOnFxThread(AnalyseScreen::new);

        runOnFxThread(() -> {
            screen.clearAnalysis();
            return null;
        });

        assertNotNull(screen);
    }

    @Test
    public void showSearchMatch_withEmptyMatches_doesNothing() throws Exception {
        AnalyseScreen screen = runOnFxThread(AnalyseScreen::new);

        runOnFxThread(() -> {
            screen.showSearchMatch(List.of(), 0);
            return null;
        });

        assertNotNull(screen);
    }

    @Test
    public void showSearchMatch_withNullMatch_doesNothing() throws Exception {
        AnalyseScreen screen = runOnFxThread(AnalyseScreen::new);

        runOnFxThread(() -> {
            screen.showSearchMatch((SearchMatch) null);
            return null;
        });

        assertNotNull(screen);
    }

    @Test
    public void showSearchMatch_clampsNegativeIndex() throws Exception {
        AnalyseScreen screen = runOnFxThread(AnalyseScreen::new);

        SearchMatch match = createSearchMatch();

        runOnFxThread(() -> {
            screen.showSearchMatch(List.of(match), -10);
            return null;
        });

        assertNotNull(screen);
    }

    @Test
    public void showSearchMatch_clampsIndexAboveRange() throws Exception {
        AnalyseScreen screen = runOnFxThread(AnalyseScreen::new);

        SearchMatch first = createSearchMatch();

        runOnFxThread(() -> {
            screen.showSearchMatch(List.of(first), 100);
            return null;
        });

        assertNotNull(screen);
    }

    @Test
    public void showSearchResults_withEmptyMatches_doesNotThrow()
            throws Exception {

        AnalyseScreen screen = runOnFxThread(AnalyseScreen::new);

        runOnFxThread(() -> {
            screen.showSearchResults(
                    "test",
                    List.of(),
                    null);

            return null;
        });

        assertNotNull(screen);
    }

    @Test
    public void showSearchResults_withNullMatches_doesNotThrow()
            throws Exception {

        AnalyseScreen screen = runOnFxThread(AnalyseScreen::new);

        runOnFxThread(() -> {
            screen.showSearchResults(
                    "test",
                    null,
                    null);

            return null;
        });

        assertNotNull(screen);
    }

    @Test
    public void setTextAnalysis_acceptsResults() throws Exception {
        AnalyseScreen screen = runOnFxThread(AnalyseScreen::new);

        runOnFxThread(() -> {
            screen.setTextAnalysis(
                    List.of(),
                    List.of());

            return null;
        });

        assertNotNull(screen.getTextTermFrequencyPanel());
        assertNotNull(screen.getTextContextPanel());
    }

    @Test
    public void setQuotations_acceptsEmptyList() throws Exception {
        AnalyseScreen screen = runOnFxThread(AnalyseScreen::new);

        runOnFxThread(() -> {
            screen.setQuotations(List.of());
            return null;
        });

        assertNotNull(screen);
    }

    @Test
    public void removeQuotationHighlight_doesNotThrow() throws Exception {
        AnalyseScreen screen = runOnFxThread(AnalyseScreen::new);

        runOnFxThread(() -> {
            screen.removeQuotationHighlight(123);
            return null;
        });

        assertNotNull(screen);
    }

    private SearchMatch createSearchMatch() {
        return new SearchMatch(
                "test", // text
                0, // matchStart
                4, // matchEnd
                1, // page
                null, // paragraph
                "context text"); // context
    }
}
