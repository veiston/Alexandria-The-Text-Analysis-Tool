package com.alexandria.view.screens;

import com.alexandria.model.FileType;
import com.alexandria.model.Text;
import com.alexandria.view.components.compare_screen.CompareHeader;
import com.alexandria.view.components.compare_screen.ComparisonDocumentView;
import com.alexandria.view.components.compare_screen.ComparisonSidePanel;
import com.alexandria.view.components.shared.SuccessToast;
import com.alexandria.service.analysis.SearchMatch;
import com.alexandria.service.analysis.SearchSettings;
import com.alexandria.service.analysis.TextComparisonResult;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.File;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.List;

public class CompareScreen extends StackPane {

    private final CompareHeader header = new CompareHeader();
    private final ComparisonDocumentView documentView = new ComparisonDocumentView();
    private final ComparisonSidePanel comparisonSidePanel = new ComparisonSidePanel();
    private final ScrollPane sidebarScroll = new ScrollPane(comparisonSidePanel);
    private final SuccessToast successToast = new SuccessToast();

    private final VBox emptyState = buildEmptyState();
    private final BorderPane loadedState = new BorderPane();
    private final StackPane centerSwitcher = new StackPane();

    private Runnable onSaveFindings = () -> {
    };
    private Consumer<String> onSearch = term -> {
    };
    private Runnable onPreviousMatch = () -> {
    };
    private Runnable onNextMatch = () -> {
    };

    /**
     * One entry per tracked row: every searched term produces a "Doc A" row and a
     * "Doc B" row.
     */
    private final Map<String, TrackedSearch> trackedSearches = new LinkedHashMap<>();
    private String activeTrackedKey;
    private String activeSearchTerm;

    public CompareScreen() {
        getStyleClass().add("compare-screen");

        emptyState.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        loadedState.setTop(header);
        loadedState.setCenter(buildBody());
        loadedState.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        getChildren().addAll(emptyState, loadedState, successToast);

        StackPane.setAlignment(emptyState, Pos.CENTER);
        StackPane.setAlignment(loadedState, Pos.CENTER);
        StackPane.setAlignment(successToast, Pos.TOP_CENTER);
        StackPane.setMargin(successToast, new Insets(24, 0, 0, 0));

        loadedState.setVisible(false);
        loadedState.setManaged(false);

        wireCallbacks();
    }

    private static final double SIDE_PANEL_WIDTH_RATIO = 0.360;

    private HBox buildBody() {
        centerSwitcher.getChildren().setAll(documentView);
        centerSwitcher.setMinSize(0, 0);
        centerSwitcher.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        HBox.setHgrow(centerSwitcher, Priority.ALWAYS);

        sidebarScroll.getStyleClass().addAll("shared-scroll", "comparison-sidebar-scroll");
        sidebarScroll.setFitToWidth(true);
        sidebarScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        sidebarScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        sidebarScroll.setMinWidth(320);
        sidebarScroll.setMaxWidth(640);

        HBox body = new HBox(0, centerSwitcher, sidebarScroll);
        body.getStyleClass().add("compare-body");
        body.setFillHeight(true);
        body.setMinSize(0, 0);
        body.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        sidebarScroll.prefWidthProperty().bind(
                body.widthProperty().multiply(SIDE_PANEL_WIDTH_RATIO));

        return body;
    }

    private void wireCallbacks() {
        header.setOnSave(() -> onSaveFindings.run());

        comparisonSidePanel.setOnCommonWordSelected(this::searchFor);
        comparisonSidePanel.getSearchView().setOnSearch(this::searchFor);
        comparisonSidePanel.getSearchView().setOnPreviousMatch(() -> onPreviousMatch.run());
        comparisonSidePanel.getSearchView().setOnNextMatch(() -> onNextMatch.run());
        comparisonSidePanel.getSearchView().getTrackedWordsList().setOnRemove(
                this::removeTrackedSearchTerm);
        comparisonSidePanel.getSearchView().getTrackedWordsList().setOnGoTo(this::goToTrackedSearch);
        comparisonSidePanel.setOnDocumentAParagraphSelected(snippet -> {
            documentView.getDocumentA().jumpToPassage(snippet.page(), snippet.text());
        });
        comparisonSidePanel.setOnDocumentBParagraphSelected(snippet -> {
            documentView.getDocumentB().jumpToPassage(snippet.page(), snippet.text());
        });
    }

    private VBox buildEmptyState() {
        Label title = new Label("No documents selected");
        title.getStyleClass().add("heading-lg");

        Label subtitle = new Label("Select two documents to compare them.");
        subtitle.getStyleClass().add("text-muted");

        VBox box = new VBox(8, title, subtitle);
        box.getStyleClass().add("empty-state");
        box.setAlignment(Pos.CENTER);
        return box;
    }

    public void loadDocuments(
            String documentATitle, String contentA, FileType fileTypeA, Path sourcePathA,
            String documentBTitle, String contentB, FileType fileTypeB, Path sourcePathB) {

        emptyState.setVisible(false);
        emptyState.setManaged(false);

        loadedState.setVisible(true);
        loadedState.setManaged(true);

        header.setSimilarity(null);

        // Tracked rows hold matches of the previously opened texts, so they must not
        // survive a new comparison.
        resetTrackedSearches();
        comparisonSidePanel.getSearchView().reset();

        documentView.loadDocuments(
                contentA, fileTypeA, sourcePathA,
                contentB, fileTypeB, sourcePathB);

        applyCss();
        layout();
    }

    public void loadTexts(Text textA, File fileA, Text textB, File fileB) {
        loadDocuments(
                displayTitle(textA), textA.getContent(), textA.getFileType(), toPath(fileA),
                displayTitle(textB), textB.getContent(), textB.getFileType(), toPath(fileB));

        documentView.setDocumentNames(textA.getFileName(), textB.getFileName());
    }

    private String displayTitle(Text text) {
        if (text.getTitle() != null && !text.getTitle().isBlank()) {
            return text.getTitle();
        }
        return text.getFileName();
    }

    private Path toPath(File file) {
        return file == null ? null : file.toPath();
    }

    public void setSimilarity(Double percent) {
        header.setSimilarity(percent);
    }

    public void setTextComparison(TextComparisonResult result) {
        if (result == null) {
            return;
        }
        header.setSimilarity(result.similarityScore(), result.similarityAmount());
        comparisonSidePanel.setTextComparison(result);
    }

    /**
     * Terms currently in the tracked list (each term once, even though it has a Doc
     * A and a Doc B row).
     */
    public List<String> getTrackedSearchTerms() {
        LinkedHashSet<String> terms = new LinkedHashSet<>();
        for (TrackedSearch tracked : trackedSearches.values()) {
            terms.add(tracked.term());
        }
        return List.copyOf(terms);
    }

    /**
     * Success toast after findings were stored; savedCount = text comparison +
     * saved terms.
     */
    public void showSaved(int savedCount) {
        int terms = Math.max(0, savedCount - 1);
        successToast.show(terms == 0
                ? "Comparison saved"
                : "Comparison and " + terms + (terms == 1 ? " term" : " terms") + " saved");
    }

    private void searchFor(String term) {
        comparisonSidePanel.getSearchView().getSearchInput().textProperty().set(term);
        beginSearch(term);
        onSearch.accept(term);
    }

    public void showSearchResults(Map<Integer, List<SearchMatch>> matchesByText, int firstTextId, int secondTextId) {
        Map<Integer, List<SearchMatch>> safeMatches = matchesByText == null ? Map.of() : matchesByText;
        String term = comparisonSidePanel.getSearchView().getSearchText();
        if (term == null || term.isBlank()) {
            return;
        }

        activeSearchTerm = term;
        String keyA = trackSearch(term, true, safeMatches.getOrDefault(firstTextId, List.of()));
        String keyB = trackSearch(term, false, safeMatches.getOrDefault(secondTextId, List.of()));

        // Start on the first document that actually has matches.
        if (!trackedSearches.get(keyA).matches().isEmpty()) {
            activateTrackedSearch(keyA);
        } else if (!trackedSearches.get(keyB).matches().isEmpty()) {
            activateTrackedSearch(keyB);
        } else {
            clearActiveSearch();
        }
    }

    private String trackSearch(String term, boolean first, List<SearchMatch> matches) {
        String key = trackedKey(term, first);
        trackedSearches.put(key, new TrackedSearch(term, first, List.copyOf(matches)));
        comparisonSidePanel.getSearchView().getTrackedWordsList().addOrUpdate(key, matches.size());
        return key;
    }

    private static String trackedKey(String term, boolean first) {
        return (first ? "Doc A · " : "Doc B · ") + term;
    }

    private void beginSearch(String term) {
        activeSearchTerm = term == null || term.isBlank() ? null : term;
    }

    private void goToTrackedSearch(String key) {
        TrackedSearch tracked = trackedSearches.get(key);
        if (tracked == null) {
            return;
        }
        // Clicking the row that is already active steps to the next match in that
        // document.
        if (key.equals(activeTrackedKey) && !tracked.matches().isEmpty()) {
            tracked.step(1);
        }
        activateTrackedSearch(key);
    }

    private void activateTrackedSearch(String key) {
        TrackedSearch tracked = trackedSearches.get(key);
        if (tracked == null) {
            return;
        }
        activeTrackedKey = key;
        comparisonSidePanel.getSearchView().getTrackedWordsList().setActiveTerm(key);
        showActiveSearchTarget(tracked);
    }

    private void showActiveSearchTarget(TrackedSearch tracked) {
        if (tracked.matches().isEmpty()) {
            documentView.clearSearchHighlightsA();
            documentView.clearSearchHighlightsB();
            return;
        }
        SearchMatch match = tracked.matches().get(tracked.activeIndex());
        if (tracked.first()) {
            documentView.clearSearchHighlightsB();
            documentView.goToPageA(match.page(), match.paragraph());
            documentView.showSearchMatchesA(tracked.matches(), tracked.activeIndex());
        } else {
            documentView.clearSearchHighlightsA();
            documentView.goToPageB(match.page(), match.paragraph());
            documentView.showSearchMatchesB(tracked.matches(), tracked.activeIndex());
        }
    }

    public void showPreviousSearchMatch() {
        moveSearchTarget(-1);
    }

    public void showNextSearchMatch() {
        moveSearchTarget(1);
    }

    /**
     * Previous/next arrows move through the matches of the active row's document
     * only.
     */
    private void moveSearchTarget(int delta) {
        TrackedSearch tracked = activeTrackedKey == null ? null : trackedSearches.get(activeTrackedKey);
        if (tracked == null || tracked.matches().isEmpty()) {
            return;
        }
        tracked.step(delta);
        showActiveSearchTarget(tracked);
    }

    private void removeTrackedSearchTerm(String key) {
        comparisonSidePanel.getSearchView().getTrackedWordsList().remove(key);
        TrackedSearch removed = trackedSearches.remove(key);
        if (removed != null && removed.term().equals(activeSearchTerm)
                && trackedSearches.values().stream().noneMatch(t -> t.term().equals(activeSearchTerm))) {
            activeSearchTerm = null;
        }
        if (key.equals(activeTrackedKey)) {
            clearActiveSearch();
        }
    }

    private void clearActiveSearch() {
        activeTrackedKey = null;
        documentView.clearSearchHighlightsA();
        documentView.clearSearchHighlightsB();
    }

    private void resetTrackedSearches() {
        trackedSearches.clear();
        activeTrackedKey = null;
        activeSearchTerm = null;
    }

    public void clearComparison() {
        documentView.clear();
        comparisonSidePanel.clear();
        resetTrackedSearches();

        loadedState.setVisible(false);
        loadedState.setManaged(false);

        emptyState.setVisible(true);
        emptyState.setManaged(true);
    }

    public void dispose() {
        documentView.dispose();
        comparisonSidePanel.clear();
    }

    public ComparisonDocumentView getDocumentView() {
        return documentView;
    }

    public ComparisonSidePanel getComparisonSidePanel() {
        return comparisonSidePanel;
    }

    public CompareHeader getHeader() {
        return header;
    }

    public void setOnSaveFindings(Runnable handler) {
        onSaveFindings = handler == null ? () -> {
        } : handler;
    }

    public void setOnSearch(Consumer<String> handler) {
        onSearch = handler == null ? term -> {
        } : handler;
    }

    public void setOnPreviousMatch(Runnable handler) {
        onPreviousMatch = handler == null ? () -> {
        } : handler;
    }

    public void setOnNextMatch(Runnable handler) {
        onNextMatch = handler == null ? () -> {
        } : handler;
    }

    public void goToDocumentAPage(Integer page, Integer paragraph) {
        documentView.goToPageA(page, paragraph);
    }

    public void goToDocumentBPage(Integer page, Integer paragraph) {
        documentView.goToPageB(page, paragraph);
    }

    public SearchSettings getSearchSettings() {
        return comparisonSidePanel.getSearchView().getSearchSettings();
    }

    /**
     * Matches of one term in one document, plus which of them is currently shown.
     */
    private static final class TrackedSearch {
        private final String term;
        private final boolean first;
        private final List<SearchMatch> matches;
        private int activeIndex;

        private TrackedSearch(String term, boolean first, List<SearchMatch> matches) {
            this.term = term;
            this.first = first;
            this.matches = matches;
        }

        private String term() {
            return term;
        }

        private boolean first() {
            return first;
        }

        private List<SearchMatch> matches() {
            return matches;
        }

        private int activeIndex() {
            return activeIndex;
        }

        private void step(int delta) {
            if (!matches.isEmpty()) {
                activeIndex = Math.floorMod(activeIndex + delta, matches.size());
            }
        }
    }
}