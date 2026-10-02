package com.alexandria.view.components.compare_screen;

import com.alexandria.service.analysis.WordFrequency;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CompareTermFrequencyPanelTest {

    @BeforeClass
    public static void startJavaFx() {
        try {
            Platform.startup(() -> {
            });
        } catch (IllegalStateException ignored) {
            // JavaFX toolkit has already been started by another test.
        }
    }

    @Test
    public void constructorShowsHeadingEmptyStateAndLegend() {
        CompareTermFrequencyPanel panel = new CompareTermFrequencyPanel();

        assertEquals(3, panel.getChildren().size());

        HBox heading = (HBox) panel.getChildren().get(0);
        assertEquals(
                "Term Frequency (Top 5)",
                ((Label) heading.getChildren().get(0)).getText()
        );
        assertEquals(
                "Relative Ratio",
                ((Label) heading.getChildren().get(2)).getText()
        );

        VBox rowsHost = rowsHost(panel);
        assertEquals(1, rowsHost.getChildren().size());
        assertEquals(
                "Open two documents to compare frequent words.",
                ((Label) rowsHost.getChildren().get(0)).getText()
        );

        HBox legend = (HBox) panel.getChildren().get(2);
        assertEquals("Doc A", ((Label) legend.getChildren().get(1)).getText());
        assertEquals("Doc B", ((Label) legend.getChildren().get(3)).getText());
    }

    @Test
    public void setResultsShowsOnlyCommonWordsAndLimitsToFive() {
        CompareTermFrequencyPanel panel = new CompareTermFrequencyPanel();

        List<WordFrequency> docA = List.of(
                frequency("alpha", 10),
                frequency("beta", 9),
                frequency("gamma", 8),
                frequency("delta", 7),
                frequency("epsilon", 6),
                frequency("zeta", 50),      // not common
                frequency("eta", 40)        // not common
        );

        List<WordFrequency> docB = List.of(
                frequency("alpha", 5),
                frequency("beta", 4),
                frequency("gamma", 3),
                frequency("delta", 2),
                frequency("epsilon", 1),
                frequency("theta", 100)     // not common
        );

        panel.setResults(docA, docB);

        VBox rowsHost = rowsHost(panel);

        assertEquals(5, rowsHost.getChildren().size());

        assertEquals("alpha", rowWord(rowsHost, 0));
        assertEquals("beta", rowWord(rowsHost, 1));
        assertEquals("gamma", rowWord(rowsHost, 2));
        assertEquals("delta", rowWord(rowsHost, 3));
        assertEquals("epsilon", rowWord(rowsHost, 4));
    }

    @Test
    public void setResultsMatchesWordsCaseInsensitivelyAndSortsByCombinedCount() {
        CompareTermFrequencyPanel panel = new CompareTermFrequencyPanel();

        List<WordFrequency> docA = List.of(
                frequency("Data", 10),
                frequency("Integration", 8),
                frequency("Efficiency", 14),
                frequency("Analysis", 6)
        );

        List<WordFrequency> docB = List.of(
                frequency("data", 9),
                frequency("INTEGRATION", 15),
                frequency("efficiency", 12),
                frequency("analysis", 20)
        );

        panel.setResults(docA, docB);

        VBox rowsHost = rowsHost(panel);

        // Sorted by combined count: Analysis 26, Efficiency 26,
        // Integration 23, Data 19. Alphabetical order breaks the 26 tie.
        assertEquals("Analysis", rowWord(rowsHost, 0));
        assertEquals("Efficiency", rowWord(rowsHost, 1));
        assertEquals("Integration", rowWord(rowsHost, 2));
        assertEquals("Data", rowWord(rowsHost, 3));
    }

    @Test
    public void setResultsUsesTheHigherCountWhenAWordAppearsMoreThanOnce() {
        CompareTermFrequencyPanel panel = new CompareTermFrequencyPanel();

        List<WordFrequency> docA = List.of(
                frequency("data", 4),
                frequency("DATA", 10),
                frequency("other", 1)
        );

        List<WordFrequency> docB = List.of(
                frequency("Data", 7)
        );

        panel.setResults(docA, docB);

        VBox rowsHost = rowsHost(panel);
        assertEquals(1, rowsHost.getChildren().size());

        HBox row = (HBox) rowsHost.getChildren().get(0);
        assertEquals("DATA", ((Label) row.getChildren().get(0)).getText());

        HBox counts = (HBox) row.getChildren().get(2);
        assertEquals("10", ((Label) counts.getChildren().get(1)).getText());
        assertEquals("7", ((Label) counts.getChildren().get(4)).getText());
    }

    @Test
    public void setResultsWithNullOrEmptyInputShowsEmptyState() {
        CompareTermFrequencyPanel panel = new CompareTermFrequencyPanel();

        panel.setResults(null, List.of(frequency("word", 1)));
        assertEmptyState(panel);

        panel.setResults(List.of(frequency("word", 1)), null);
        assertEmptyState(panel);

        panel.setResults(List.of(), List.of(frequency("word", 1)));
        assertEmptyState(panel);

        panel.setResults(List.of(frequency("word", 1)), List.of());
        assertEmptyState(panel);
    }

    @Test
    public void setComparisonResultsLimitsResultsToFive() {
        CompareTermFrequencyPanel panel = new CompareTermFrequencyPanel();

        List<CompareTermFrequencyPanel.ComparisonWord> words = List.of(
                comparison("one", 1, 1),
                comparison("two", 2, 2),
                comparison("three", 3, 3),
                comparison("four", 4, 4),
                comparison("five", 5, 5),
                comparison("six", 6, 6)
        );

        panel.setComparisonResults(words);

        VBox rowsHost = rowsHost(panel);
        assertEquals(5, rowsHost.getChildren().size());
        assertEquals("one", rowWord(rowsHost, 0));
        assertEquals("five", rowWord(rowsHost, 4));
    }

    @Test
    public void setComparisonResultsWithNullOrEmptyInputShowsEmptyState() {
        CompareTermFrequencyPanel panel = new CompareTermFrequencyPanel();

        panel.setComparisonResults(null);
        assertEmptyState(panel);

        panel.setComparisonResults(List.of());
        assertEmptyState(panel);
    }

    @Test
    public void setDocumentNamesUpdatesLegendLabels() {
        CompareTermFrequencyPanel panel = new CompareTermFrequencyPanel();

        panel.setDocumentNames("first-document.txt", "second-document.txt");

        HBox legend = (HBox) panel.getChildren().get(2);

        assertEquals(
                "Doc A: first-document.txt",
                ((Label) legend.getChildren().get(1)).getText()
        );

        assertEquals(
                "Doc B: second-document.txt",
                ((Label) legend.getChildren().get(3)).getText()
        );
    }

    @Test
    public void setDocumentNamesFallsBackToDocumentLabelForBlankName() {
        CompareTermFrequencyPanel panel = new CompareTermFrequencyPanel();

        panel.setDocumentNames("", "   ");

        HBox legend = (HBox) panel.getChildren().get(2);

        assertEquals("Doc A", ((Label) legend.getChildren().get(1)).getText());
        assertEquals("Doc B", ((Label) legend.getChildren().get(3)).getText());
    }

    @Test
    public void rowContainsTwoDocumentBarsAndNoTrack() {
        CompareTermFrequencyPanel panel = new CompareTermFrequencyPanel();

        panel.setComparisonResults(List.of(
                comparison("data", 10, 5)
        ));

        HBox row = (HBox) rowsHost(panel).getChildren().get(0);
        StackPane frequencyBar = (StackPane) row.getChildren().get(1);

        assertEquals(2, frequencyBar.getChildren().size());
        assertEquals(
                "doc-a-bar",
                frequencyBar.getChildren().get(0).getStyleClass().get(0)
        );
        assertEquals(
                "doc-b-bar",
                frequencyBar.getChildren().get(1).getStyleClass().get(0)
        );

        assertFalse(
                frequencyBar.getChildren().stream()
                        .anyMatch(node -> node.getStyleClass().contains("track"))
        );
    }

    @Test
    public void rowClickCallsRegisteredHandlerWithWord() {
        CompareTermFrequencyPanel panel = new CompareTermFrequencyPanel();
        AtomicReference<String> clickedWord = new AtomicReference<>();

        panel.setOnRowClick(clickedWord::set);
        panel.setComparisonResults(List.of(
                comparison("efficiency", 14, 12)
        ));

        HBox row = (HBox) rowsHost(panel).getChildren().get(0);

        // The component's click handler only uses the captured word,
        // so invoking the registered handler directly tests the wiring
        // without requiring a synthetic JavaFX mouse event.
        row.getOnMouseClicked().handle(null);

        assertEquals("efficiency", clickedWord.get());
    }

    private static VBox rowsHost(CompareTermFrequencyPanel panel) {
        return (VBox) panel.getChildren().get(1);
    }

    private static String rowWord(VBox rowsHost, int index) {
        HBox row = (HBox) rowsHost.getChildren().get(index);
        return ((Label) row.getChildren().get(0)).getText();
    }

    private static void assertEmptyState(CompareTermFrequencyPanel panel) {
        VBox rowsHost = rowsHost(panel);

        assertEquals(1, rowsHost.getChildren().size());
        assertTrue(rowsHost.getChildren().get(0) instanceof Label);
        assertEquals(
                "Open two documents to compare frequent words.",
                ((Label) rowsHost.getChildren().get(0)).getText()
        );
    }

    private static WordFrequency frequency(String word, int count) {
        return new WordFrequency(
                word,
                count,
                0.0,
                null,
                null
        );
    }

    private static CompareTermFrequencyPanel.ComparisonWord comparison(
            String word,
            int docACount,
            int docBCount) {

        return new CompareTermFrequencyPanel.ComparisonWord(
                word,
                docACount,
                docBCount
        );
    }
}