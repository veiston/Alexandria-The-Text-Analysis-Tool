package com.alexandria.view.screens;

import com.alexandria.model.FileType;
import com.alexandria.service.analysis.SearchMatch;
import com.alexandria.service.analysis.TermAnalysisResult;
import com.alexandria.service.analysis.TextFragment;
import com.alexandria.service.analysis.WordFrequency;
import com.alexandria.view.components.analyse_screen.AnalyseHeader;
import com.alexandria.view.components.analyse_screen.DocumentView;
import com.alexandria.view.components.analyse_screen.QuotationsView;
import com.alexandria.view.components.analyse_screen.SearchView;
import com.alexandria.view.components.analyse_screen.TermDetailModal;
import com.alexandria.view.components.analyse_screen.text_term_analyze.TextContextPanel;
import com.alexandria.view.components.analyse_screen.text_term_analyze.TextTermFrequencyPanel;
import com.alexandria.view.components.shared.modal.Modal;

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

    private Runnable onSaveAnalysis = () -> {};
    private Consumer<String> onTermDetailRequested = term -> {};

    public AnalyseScreen() {
        getStyleClass().add("analyse-screen");

        emptyState.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        loadedState.setTop(header);
        loadedState.setCenter(buildBody());
        loadedState.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        getChildren().addAll(emptyState, loadedState, modal);
        StackPane.setAlignment(emptyState, Pos.CENTER);
        StackPane.setAlignment(loadedState, Pos.CENTER);
        StackPane.setAlignment(modal, Pos.CENTER);

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

        ScrollPane sidebarScroll = new ScrollPane(rightColumn);
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
        textContextPanel.setOnJump(documentView::goToPage);
    }

    private void showView(int index) {
        switch (index) {
            case 1 -> centerSwitcher.getChildren().setAll(quotationsView);
            default -> centerSwitcher.getChildren().setAll(documentView);
        }
    }

    private VBox buildEmptyState() {

        Label title = new Label("No document open");
        title.getStyleClass().add("heading-lg");

        Label subtitle = new Label(
                "Start a new project or open one from your Library to begin analysing."
        );
        subtitle.getStyleClass().add("text-muted");

        VBox box = new VBox(8, title, subtitle);
        box.getStyleClass().add("analyse-empty-state");
        box.setAlignment(Pos.CENTER);

        return box;
    }

    public void loadDocument(
            String projectTitle,
            String fileName,
            String content,
            FileType fileType,
            Path sourcePath,
            List<Integer> pageOffsets
    ) {

        header.setTitle(
                projectTitle,
                fileName + (pageOffsets != null && !pageOffsets.isEmpty()
                        ? " · " + pageOffsets.size() + " Pages"
                        : "")
        );

        header.resetToReader();

        documentView.loadDocument(content, fileType, sourcePath);

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

    public void setOnTermDetailRequested(Consumer<String> handler) {
        onTermDetailRequested = handler == null ? term -> {} : handler;
    }

    public void setOnSaveAnalysis(Runnable handler) {
        onSaveAnalysis = handler == null ? () -> {} : handler;
    }

    public void showTermDetail(String term, TermAnalysisResult analysis, List<SearchMatch> matches) {
        termDetailModal.setData(term, analysis, matches, index -> {
            // TODO: documentView.jumpToMatch(index) once real document
            // navigation + highlighting exists.
        });
        modal.show(termDetailModal);
    }
}