package com.alexandria.view.screens;

import com.alexandria.model.FileType;
import com.alexandria.model.Quotation;
import com.alexandria.view.components.shared.quotation.QuotationLocation;
import com.alexandria.service.analysis.SearchMatch;
import com.alexandria.service.analysis.TermAnalysisResult;
import com.alexandria.service.analysis.TextFragment;
import com.alexandria.service.analysis.WordFrequency;
import com.alexandria.view.components.analyse_screen.AnalyseHeader;
import com.alexandria.view.components.shared.document.DocumentView;
import com.alexandria.view.components.shared.quotation.QuotationsView;
import com.alexandria.view.components.shared.search.SearchView;
import com.alexandria.view.components.analyse_screen.TermDetailModal;
import com.alexandria.view.components.analyse_screen.text_term_analyze.TextContextPanel;
import com.alexandria.view.components.analyse_screen.text_term_analyze.TextTermFrequencyPanel;
import com.alexandria.view.components.shared.modal.Modal;
import com.alexandria.view.components.shared.SuccessToast;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.nio.file.Path;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class AnalyseScreen extends StackPane {

    private final AnalyseHeader header = new AnalyseHeader();
    private final DocumentView documentView = new DocumentView();
    private final SearchView searchView = new SearchView();
    private final TextTermFrequencyPanel textTermFrequencyPanel = new TextTermFrequencyPanel();
    private final TextContextPanel textContextPanel = new TextContextPanel();
    private final QuotationsView quotationsView = new QuotationsView();
    private final TermDetailModal termDetailModal = new TermDetailModal();
    private final Modal modal = new Modal();
    private final VBox emptyState = buildEmptyState();
    private final BorderPane loadedState = new BorderPane();
    private final StackPane centerSwitcher = new StackPane();
    private final ScrollPane sidebarScroll = new ScrollPane();
    private final SuccessToast successToast = new SuccessToast();

    private String activeSearchTerm;
    private List<SearchMatch> currentSearchMatches = List.of();
    private int currentSearchMatchIndex = -1;
    private Runnable onSaveAnalysis = () -> {
    };
    private Consumer<String> onTermDetailRequested = term -> {
    };
    private BiFunction<String, String, Integer> onQuotationRequested = (text, location) -> null;
    private Consumer<Quotation> onQuotationDeleteRequested = quotation -> {
    };
    private Consumer<Quotation> onQuotationEditRequested = quotation -> {
    };
    private Consumer<String> onSearch = term -> {
    };
    private Runnable onPreviousMatch = () -> {
    };
    private Runnable onNextMatch = () -> {
    };
    private Consumer<String> onTrackedTermRemoved = term -> {
    };

    public AnalyseScreen() {
        getStyleClass().add("analyse-screen");
        emptyState.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        loadedState.setTop(header);
        loadedState.setCenter(buildBody());
        loadedState.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        getChildren().addAll(emptyState, loadedState, modal, successToast);
        StackPane.setAlignment(emptyState, Pos.CENTER);
        StackPane.setAlignment(loadedState, Pos.CENTER);
        StackPane.setAlignment(modal, Pos.CENTER);
        StackPane.setAlignment(successToast, Pos.TOP_CENTER);
        StackPane.setMargin(successToast, new Insets(16, 0, 0, 0));
        loadedState.setVisible(false);
        loadedState.setManaged(false);
        wireCallbacks();
    }

    private HBox buildBody() {
        centerSwitcher.getChildren().setAll(documentView);
        centerSwitcher.setMinSize(0, 0);
        centerSwitcher.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        HBox.setHgrow(centerSwitcher, Priority.ALWAYS);

        VBox frequencyAndSearch = new VBox(18, textTermFrequencyPanel, searchView);
        frequencyAndSearch.getStyleClass().add("card");

        VBox rightColumn = new VBox(16, frequencyAndSearch, textContextPanel);
        rightColumn.getStyleClass().add("analyse-sidebar");
        rightColumn.setMinWidth(340);
        rightColumn.setPrefWidth(380);
        rightColumn.setMaxWidth(440);
        VBox.setVgrow(textContextPanel, Priority.ALWAYS);

        sidebarScroll.setContent(rightColumn);
        sidebarScroll.getStyleClass().add("analyse-sidebar-scroll");
        sidebarScroll.setFitToWidth(true);
        sidebarScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        sidebarScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        sidebarScroll.setMinWidth(340);
        sidebarScroll.setPrefWidth(380);
        sidebarScroll.setMaxWidth(440);

        HBox body = new HBox(0, centerSwitcher, sidebarScroll);
        body.getStyleClass().add("analyse-body");
        body.setFillHeight(true);
        body.setMinSize(0, 0);
        body.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        return body;
    }

    private void wireCallbacks() {
        header.setOnSave(() -> onSaveAnalysis.run());
        header.setOnViewChange(this::showView);
        textTermFrequencyPanel.setOnRowClick(word -> onTermDetailRequested.accept(word));
        documentView.setOnQuotationRequested((text, location) -> onQuotationRequested.apply(text, location));

        quotationsView.setOnGoTo(this::goToQuotation);
        quotationsView.setOnDelete(quotation -> onQuotationDeleteRequested.accept(quotation));
        quotationsView.setOnEdit(quotation -> onQuotationEditRequested.accept(quotation));

        textContextPanel.setOnJump(fragment -> {
            if (fragment == null || fragment.page() == null)
                return;
            showDocument();
            documentView.jumpToPassage(fragment.page(), fragment.text());
        });

        searchView.setOnSearch(term -> onSearch.accept(term));
        searchView.setOnPreviousMatch(() -> onPreviousMatch.run());
        searchView.setOnNextMatch(() -> onNextMatch.run());

        searchView.getTrackedWordsList().setOnInfo(word -> onTermDetailRequested.accept(word));
        searchView.getTrackedWordsList().setOnRemove(word -> {
            searchView.getTrackedWordsList().remove(word);
            onTrackedTermRemoved.accept(word);
            if (word.equals(activeSearchTerm)) {
                resetSearchState();
                documentView.clearSearchHighlights();
            }
        });
        searchView.getTrackedWordsList().setOnGoTo(word -> {
            searchView.getSearchInput().textProperty().set(word);
            onSearch.accept(word);
        });
    }

    private void resetSearchState() {
        activeSearchTerm = null;
        currentSearchMatches = List.of();
        currentSearchMatchIndex = -1;
    }

    private void showView(int index) {
        centerSwitcher.getChildren().setAll(index == 1 ? quotationsView : documentView);
    }

    private void showDocument() {
        header.resetToReader();
        centerSwitcher.getChildren().setAll(documentView);
    }

    private VBox buildEmptyState() {
        Label title = new Label("No document open");
        title.getStyleClass().add("heading-lg");
        Label subtitle = new Label("Start a new project or open one from your Library to begin analysing.");
        subtitle.getStyleClass().add("text-muted");
        VBox box = new VBox(8, title, subtitle);
        box.getStyleClass().add("analyse-empty-state");
        box.setAlignment(Pos.CENTER);
        return box;
    }

    public void loadDocument(String projectTitle, String fileName, String content,
            FileType fileType, Path sourcePath, List<Integer> pageOffsets) {

        boolean hasPages = pageOffsets != null && !pageOffsets.isEmpty();
        header.setTitle(projectTitle, fileName + (hasPages ? " · " + pageOffsets.size() + " Pages" : ""));
        header.resetToReader();
        documentView.loadDocument(content, fileType, sourcePath);
        resetSearchState();
        searchView.reset();
        textTermFrequencyPanel.setResults(List.of());
        textContextPanel.setResults(List.of());
        emptyState.setVisible(false);
        emptyState.setManaged(false);
        loadedState.setVisible(true);
        loadedState.setManaged(true);
        showView(0);
    }

    public void setTextAnalysis(List<WordFrequency> frequentWords, List<TextFragment> fragments) {
        textTermFrequencyPanel.setResults(frequentWords);
        textContextPanel.setResults(fragments);
    }

    public void clearAnalysis() {
        modal.hide();
        documentView.dispose();
        loadedState.setVisible(false);
        loadedState.setManaged(false);
        emptyState.setVisible(true);
        emptyState.setManaged(true);
        resetSearchState();
    }

    public void showSaved(int savedCount) {
        successToast.show(savedCount > 0 ? "Analysis saved" : "Analysis already saved");
    }

    private static <T> Consumer<T> orNoop(Consumer<T> handler) {
        return handler == null ? value -> {
        } : handler;
    }

    public void setOnTermDetailRequested(Consumer<String> handler) {
        onTermDetailRequested = orNoop(handler);
    }

    public void setOnTrackedTermRemoved(Consumer<String> handler) {
        onTrackedTermRemoved = orNoop(handler);
    }

    public void setOnQuotationEditRequested(Consumer<Quotation> handler) {
        onQuotationEditRequested = orNoop(handler);
    }

    public void setOnQuotationDeleteRequested(Consumer<Quotation> handler) {
        onQuotationDeleteRequested = orNoop(handler);
    }

    public void setOnSearch(Consumer<String> handler) {
        onSearch = orNoop(handler);
    }

    public void setOnSaveAnalysis(Runnable handler) {
        onSaveAnalysis = handler == null ? () -> {
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

    public void setOnQuotationRequested(BiFunction<String, String, Integer> handler) {
        onQuotationRequested = handler == null ? (text, location) -> null : handler;
    }

    public void setQuotations(List<Quotation> quotations) {
        quotationsView.setQuotations(quotations);
    }

    public void removeQuotationHighlight(int quotationId) {
        documentView.removeQuotationHighlight(quotationId);
    }

    public void showTermDetail(String term, TermAnalysisResult analysis, List<SearchMatch> matches) {
        termDetailModal.setData(term, analysis, matches, index -> {
            if (matches == null || index < 0 || index >= matches.size())
                return;
            SearchMatch match = matches.get(index);
            modal.hide();
            showDocument();
            documentView.goToPage(match.page(), match.paragraph());
            documentView.showSearchMatches(matches, index);
        });
        modal.show(termDetailModal);
    }

    public DocumentView getDocumentView() {
        return documentView;
    }

    public TextTermFrequencyPanel getTextTermFrequencyPanel() {
        return textTermFrequencyPanel;
    }

    public SearchView getSearchView() {
        return searchView;
    }

    public TextContextPanel getTextContextPanel() {
        return textContextPanel;
    }

    public ScrollPane getStatisticsSidebar() {
        return sidebarScroll;
    }

    public AnalyseHeader getHeader() {
        return header;
    }

    public void showSearchResults(String term, List<SearchMatch> matches, TermAnalysisResult termAnalysis) {
        if (matches == null || matches.isEmpty()) {
            resetSearchState();
            documentView.clearSearchHighlights();
            return;
        }
        activeSearchTerm = term;
        currentSearchMatches = List.copyOf(matches);
        currentSearchMatchIndex = 0;
        searchView.getTrackedWordsList().addOrUpdate(term, matches.size());
        searchView.getTrackedWordsList().setActiveTerm(term);
        showSearchMatch(currentSearchMatches, currentSearchMatchIndex);
    }

    public void showSearchMatch(List<SearchMatch> matches, int activeIndex) {
        if (matches == null || matches.isEmpty())
            return;
        int safeIndex = Math.max(0, Math.min(activeIndex, matches.size() - 1));
        SearchMatch activeMatch = matches.get(safeIndex);
        currentSearchMatches = List.copyOf(matches);
        currentSearchMatchIndex = safeIndex;
        if (activeMatch.page() != null)
            documentView.goToPage(activeMatch.page(), activeMatch.paragraph());
        documentView.showSearchMatches(matches, safeIndex);
    }

    public void showSearchMatch(SearchMatch match) {
        if (match == null)
            return;
        int index = currentSearchMatches.indexOf(match);
        if (index < 0) {
            if (match.page() != null)
                documentView.goToPage(match.page(), match.paragraph());
            return;
        }
        currentSearchMatchIndex = index;
        showSearchMatch(currentSearchMatches, index);
    }

    private void goToQuotation(Quotation quotation) {
        if (quotation == null)
            return;
        showDocument();
        documentView.goToPage(
                QuotationLocation.parsePage(quotation.getLocation()),
                QuotationLocation.parseStartOffset(quotation.getLocation()));
    }
}