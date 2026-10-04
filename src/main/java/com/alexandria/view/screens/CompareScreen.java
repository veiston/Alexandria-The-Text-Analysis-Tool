package com.alexandria.view.screens;

import com.alexandria.model.FileType;
import com.alexandria.model.Text;
import com.alexandria.model.Quotation;
import com.alexandria.view.components.shared.quotation.QuotationLocation;
import com.alexandria.view.components.shared.quotation.QuotationsView;
import com.alexandria.view.components.compare_screen.CompareHeader;
import com.alexandria.view.components.compare_screen.ComparisonDocumentView;
import com.alexandria.view.components.compare_screen.ComparisonSidePanel;
import com.alexandria.service.analysis.SearchMatch;
import com.alexandria.service.analysis.SearchSettings;
import com.alexandria.service.analysis.TermComparisonResult;
import com.alexandria.service.analysis.TextComparisonResult;

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
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class CompareScreen extends StackPane {

    private final CompareHeader header = new CompareHeader();
    private final ComparisonDocumentView documentView = new ComparisonDocumentView();
    private final ComparisonSidePanel comparisonSidePanel = new ComparisonSidePanel();
    private final QuotationsView quotationsView = new QuotationsView();
    private final ScrollPane sidebarScroll = new ScrollPane(comparisonSidePanel);

    private final VBox emptyState = buildEmptyState();
    private final BorderPane loadedState = new BorderPane();
    private final StackPane centerSwitcher = new StackPane();

    private BiFunction<String, String, Integer> onQuotationRequested = (text, location) -> null;
    private Runnable onSaveFindings = () -> {
    };
    private Consumer<String> onCompareTerm = term -> { };
    private Consumer<String> onSearch = term -> { };
    private Runnable onPreviousMatch = () -> { };
    private Runnable onNextMatch = () -> { };
    private List<SearchTarget> searchTargets = List.of();
    private List<SearchMatch> searchMatchesA = List.of();
    private List<SearchMatch> searchMatchesB = List.of();
    private int activeSearchTarget = -1;
    private String activeSearchTerm;
    private Integer documentAId;
    private Integer documentBId;
    private BiFunction<String, String, Integer> onDocumentAQuotationRequested = (text, location) -> null;
    private BiFunction<String, String, Integer> onDocumentBQuotationRequested = (text, location) -> null;
    private Consumer<Quotation> onQuotationDeleteRequested = quotation -> { };
    private Consumer<Quotation> onQuotationEditRequested = quotation -> { };

    public CompareScreen() {
        getStyleClass().add("compare-screen");

        emptyState.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        loadedState.setTop(header);
        loadedState.setCenter(buildBody());
        loadedState.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        getChildren().addAll(emptyState, loadedState);

        StackPane.setAlignment(emptyState, Pos.CENTER);
        StackPane.setAlignment(loadedState, Pos.CENTER);

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
        header.setOnViewChange(index -> {
            if (index == 1) {
                showQuotations();
            } else {
                showReader();
            }
        });

        documentView.setOnDocumentAQuotationRequested(
                (text, location) -> onDocumentAQuotationRequested.apply(text, location));
        documentView.setOnDocumentBQuotationRequested(
                (text, location) -> onDocumentBQuotationRequested.apply(text, location));
        quotationsView.setOnGoTo(this::goToQuotation);
        quotationsView.setOnDelete(quotation -> onQuotationDeleteRequested.accept(quotation));
        quotationsView.setOnEdit(quotation -> onQuotationEditRequested.accept(quotation));
        comparisonSidePanel.setOnCommonWordSelected(term -> onCompareTerm.accept(term));
        comparisonSidePanel.setOnTermRequested(term -> onCompareTerm.accept(term));
        comparisonSidePanel.getSearchView().setOnSearch(term -> {
            beginSearch(term);
            onSearch.accept(term);
        });
        comparisonSidePanel.getSearchView().setOnPreviousMatch(() -> onPreviousMatch.run());
        comparisonSidePanel.getSearchView().setOnNextMatch(() -> onNextMatch.run());
        comparisonSidePanel.getSearchView().getTrackedWordsList().setOnInfo(
                term -> onCompareTerm.accept(term));
        comparisonSidePanel.getSearchView().getTrackedWordsList().setOnRemove(
                this::removeTrackedSearchTerm);
        comparisonSidePanel.getSearchView().getTrackedWordsList().setOnGoTo(term -> {
            comparisonSidePanel.getSearchView().getSearchInput().textProperty().set(term);
            beginSearch(term);
            onSearch.accept(term);
        });
        comparisonSidePanel.setOnDocumentAParagraphSelected(snippet -> {
            documentView.getDocumentA().jumpToPassage(snippet.page(), snippet.text());
        });
        comparisonSidePanel.setOnDocumentBParagraphSelected(snippet -> {
            documentView.getDocumentB().jumpToPassage(snippet.page(), snippet.text());
        });
    }

    private void showReader() {
        centerSwitcher.getChildren().setAll(documentView);
    }

    private void showQuotations() {
        centerSwitcher.getChildren().setAll(quotationsView);
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
        header.resetToReader();
        showReader();

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
        documentAId = comparisonTextId(textA, 0);
        documentBId = comparisonTextId(textB, 1);
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

    public void setTermComparison(TermComparisonResult result) {
        comparisonSidePanel.setTermComparison(result);
    }

    public void setQuotations(List<Quotation> quotations) {
        quotationsView.setQuotations(quotations);
    }

    public void showSearchResults(Map<Integer, List<SearchMatch>> matchesByText, int firstTextId, int secondTextId) {
        List<SearchTarget> targets = new ArrayList<>();
        Map<Integer, List<SearchMatch>> safeMatches = matchesByText == null ? Map.of() : matchesByText;
        searchMatchesA = List.copyOf(safeMatches.getOrDefault(firstTextId, List.of()));
        searchMatchesB = List.copyOf(safeMatches.getOrDefault(secondTextId, List.of()));
        addTargets(targets, true, searchMatchesA);
        addTargets(targets, false, searchMatchesB);
        searchTargets = List.copyOf(targets);
        activeSearchTarget = searchTargets.isEmpty() ? -1 : 0;
        if (activeSearchTarget >= 0) {
            activeSearchTerm = comparisonSidePanel.getSearchView().getSearchText();
            comparisonSidePanel.getSearchView().getTrackedWordsList().addOrUpdate(
                    activeSearchTerm, searchTargets.size());
            comparisonSidePanel.getSearchView().getTrackedWordsList().setActiveTerm(activeSearchTerm);
            showActiveSearchTarget();
        } else {
            activeSearchTerm = null;
            documentView.clearSearchHighlightsA();
            documentView.clearSearchHighlightsB();
        }
    }

    private void beginSearch(String term) {
        activeSearchTerm = term == null || term.isBlank() ? null : term;
        searchTargets = List.of();
        searchMatchesA = List.of();
        searchMatchesB = List.of();
        activeSearchTarget = -1;
        documentView.clearSearchHighlightsA();
        documentView.clearSearchHighlightsB();
    }

    private void addTargets(List<SearchTarget> targets, boolean first, List<SearchMatch> matches) {
        if (matches != null) {
            for (int index = 0; index < matches.size(); index++) {
                targets.add(new SearchTarget(first, matches.get(index), index));
            }
        }
    }

    private void showActiveSearchTarget() {
        SearchTarget target = searchTargets.get(activeSearchTarget);
        if (target.first()) {
            documentView.goToPageA(target.match().page(), target.match().paragraph());
            documentView.showSearchMatchesA(searchMatchesA, target.indexInDocument());
        } else {
            documentView.goToPageB(target.match().page(), target.match().paragraph());
            documentView.showSearchMatchesB(searchMatchesB, target.indexInDocument());
        }
    }

    public void showPreviousSearchMatch() { moveSearchTarget(-1); }
    public void showNextSearchMatch() { moveSearchTarget(1); }

    private void moveSearchTarget(int delta) {
        if (searchTargets.isEmpty()) {
            return;
        }
        activeSearchTarget = Math.floorMod(activeSearchTarget + delta, searchTargets.size());
        showActiveSearchTarget();
    }

    private void removeTrackedSearchTerm(String term) {
        comparisonSidePanel.getSearchView().getTrackedWordsList().remove(term);
        if (!term.equals(activeSearchTerm)) {
            return;
        }
        activeSearchTerm = null;
        documentAId = null;
        documentBId = null;
        searchTargets = List.of();
        searchMatchesA = List.of();
        searchMatchesB = List.of();
        activeSearchTarget = -1;
        documentView.clearSearchHighlightsA();
        documentView.clearSearchHighlightsB();
    }

    public void clearComparison() {
        documentView.clear();
        comparisonSidePanel.clear();
        searchTargets = List.of();
        searchMatchesA = List.of();
        searchMatchesB = List.of();
        activeSearchTarget = -1;
        activeSearchTerm = null;

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

    public void setOnQuotationRequested(BiFunction<String, String, Integer> handler) {
        onQuotationRequested = handler == null ? (text, location) -> null : handler;
    }

    public void setOnDocumentAQuotationRequested(BiFunction<String, String, Integer> handler) { onDocumentAQuotationRequested = handler == null ? (text, location) -> null : handler; }
    public void setOnDocumentBQuotationRequested(BiFunction<String, String, Integer> handler) { onDocumentBQuotationRequested = handler == null ? (text, location) -> null : handler; }
    public void setOnQuotationDeleteRequested(Consumer<Quotation> handler) { onQuotationDeleteRequested = handler == null ? quotation -> { } : handler; }
    public void setOnQuotationEditRequested(Consumer<Quotation> handler) { onQuotationEditRequested = handler == null ? quotation -> { } : handler; }

    public void setOnSaveFindings(Runnable handler) {
        onSaveFindings = handler == null ? () -> {
        } : handler;
    }

    public void setOnCompareTerm(Consumer<String> handler) {
        onCompareTerm = handler == null ? term -> { } : handler;
    }

    public void setOnSearch(Consumer<String> handler) {
        onSearch = handler == null ? term -> { } : handler;
    }

    public void setOnPreviousMatch(Runnable handler) {
        onPreviousMatch = handler == null ? () -> { } : handler;
    }

    public void setOnNextMatch(Runnable handler) {
        onNextMatch = handler == null ? () -> { } : handler;
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

    private void goToQuotation(Quotation quotation) {
        if (quotation == null) return;
        header.resetToReader();
        showReader();
        if (java.util.Objects.equals(quotation.getTextId(), documentAId)) {
            documentView.goToPageA(QuotationLocation.parsePage(quotation.getLocation()), QuotationLocation.parseStartOffset(quotation.getLocation()));
        } else if (java.util.Objects.equals(quotation.getTextId(), documentBId)) {
            documentView.goToPageB(QuotationLocation.parsePage(quotation.getLocation()), QuotationLocation.parseStartOffset(quotation.getLocation()));
        }
    }

    private int comparisonTextId(Text text, int index) { return text.getId() == null ? -index - 1 : text.getId(); }

    private record SearchTarget(boolean first, SearchMatch match, int indexInDocument) { }
}