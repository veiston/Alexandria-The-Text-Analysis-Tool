package com.alexandria.view.components.shared.document;

import com.alexandria.service.analysis.SearchMatch;
import com.alexandria.view.components.shared.quotation.QuotationLocation;
import com.alexandria.view.components.shared.document.highlight.TextPaginator;
import com.alexandria.view.components.shared.document.highlight.TxtHighlight;
import com.alexandria.view.components.shared.document.highlight.TxtTextLayout;
import com.alexandria.view.components.shared.selection.QuotationSelectionPopup;

import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.geometry.Bounds;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class TextDocumentRenderer {
    private static final double BASE_PAGE_WIDTH = 620.0;
    private static final String LIVE_SELECTION_STYLE_CLASS = "txt-selection-rect";

    private final ScrollPane scrollPane = new ScrollPane();
    private final VBox pagesHost = new VBox(24);
    private final Pane selectionOverlay = new Pane();
    private final StackPane container = new StackPane();
    private final Map<Integer, Integer> quotationPageById = new HashMap<>();
    private final Map<Integer, TxtHighlight.Range> quotationRangeById = new HashMap<>();
    private final Map<Integer, TextFlow> pageFlows = new HashMap<>();
    private final Map<Integer, Pane> pageOverlays = new HashMap<>();
    private final Map<Integer, String> pageTexts = new HashMap<>();

    private String content = "";
    private List<Integer> pageStarts = List.of();
    private int currentVisiblePage = 1;
    private double zoom = 1.0;
    private int activeSelectionPage = -1;
    private int selectionStartIndex = -1;
    private int pendingQuotationPage = -1;
    private QuotationSelectionPopup quotationPopup;
    private Consumer<Integer> onVisiblePageChanged = ignored -> {
    };
    private BiFunction<String, String, Integer> onQuotationRequested = (quotationText, location) -> null;

    public TextDocumentRenderer(String content) {
        pagesHost.setAlignment(Pos.TOP_CENTER);
        pagesHost.getStyleClass().add("document-pages");

        scrollPane.setContent(pagesHost);
        scrollPane.setFitToWidth(false);
        scrollPane.setFitToHeight(false);
        scrollPane.setPannable(true);
        scrollPane.getStyleClass().add("viewer-scroll");
        scrollPane.vvalueProperty().addListener((obs, oldValue, newValue) -> updateVisiblePage());

        selectionOverlay.setPickOnBounds(false);
        selectionOverlay.prefWidthProperty().bind(container.widthProperty());
        selectionOverlay.prefHeightProperty().bind(container.heightProperty());

        container.getChildren().addAll(scrollPane, selectionOverlay);

        setText(content);
    }

    private void setText(String content) {
        this.content = content == null ? "" : content;
        pageStarts = TextPaginator.paginate(this.content, TextPaginator.CHARS_PER_PAGE);

        if (pageStarts.isEmpty()) {
            pageStarts = List.of(0);
        }

        rebuild();

        Platform.runLater(() -> {
            updatePageSizes();
            resetScrollPosition();
            updateVisiblePage();
        });
    }

    private void rebuild() {
        pagesHost.getChildren().clear();
        pageFlows.clear();
        pageOverlays.clear();
        pageTexts.clear();
        pendingQuotationPage = -1;

        for (int i = 0; i < pageStarts.size(); i++) {
            int pageIndex = i;
            int start = pageStarts.get(i);
            int end = i + 1 < pageStarts.size()
                    ? pageStarts.get(i + 1)
                    : content.length();

            end = Math.min(end, content.length());

            String pageText = content.substring(start, end);
            pageTexts.put(pageIndex, pageText);

            Text text = new Text(pageText);
            text.getStyleClass().add("document-page-content");
            TextFlow flow = new TextFlow(text);

            flow.getStyleClass().add("document-page-text");
            flow.setCursor(Cursor.TEXT);

            Pane highlightOverlay = new Pane();
            highlightOverlay.setMouseTransparent(true);
            highlightOverlay.setPickOnBounds(false);

            StackPane pageStack = new StackPane(highlightOverlay, flow);
            pageStack.setAlignment(Pos.TOP_LEFT);
            pageStack.getStyleClass().add("document-page");

            flow.setOnMousePressed(event -> beginSelection(pageIndex, flow, event));
            flow.setOnMouseDragged(event -> updateSelection(pageIndex, flow, highlightOverlay, event));
            flow.setOnMouseReleased(event -> endSelection(
                    pageIndex,
                    flow,
                    pageText,
                    highlightOverlay,
                    event));

            pageFlows.put(pageIndex, flow);
            pageOverlays.put(pageIndex, highlightOverlay);

            applyQuotationHighlights(pageIndex, flow, highlightOverlay);
            pagesHost.getChildren().add(pageStack);
        }
    }

    private void updatePageSizes() {
        double pageWidth = BASE_PAGE_WIDTH * zoom;
        double viewportWidth = scrollPane.getViewportBounds().getWidth();

        pagesHost.setPrefWidth(Math.max(pageWidth, viewportWidth));

        for (Node node : pagesHost.getChildren()) {
            if (node instanceof StackPane pageStack) {
                pageStack.setPrefWidth(pageWidth);
                pageStack.setMinWidth(pageWidth);
                pageStack.setMaxWidth(pageWidth);
            }
        }
    }

    private void resetScrollPosition() {
        scrollPane.setVvalue(0);
        scrollPane.setHvalue(0);

        Platform.runLater(() -> {
            scrollPane.setVvalue(0);
            scrollPane.setHvalue(0);
        });
    }

    public Node getNode() {
        return container;
    }

    public void goToPage(Integer page, Integer paragraph) {
        if (page == null || pagesHost.getChildren().isEmpty()) {
            return;
        }

        int safe = Math.max(
                0,
                Math.min(
                        page - 1,
                        pagesHost.getChildren().size() - 1));

        Node pageNode = pagesHost.getChildren().get(safe);

        Platform.runLater(() -> {
            updatePageSizes();
            centerPage(pageNode);
        });
    }

    public void highlightPassage(Integer page, String passage) {
        clearSearchHighlights();
        if (page == null || passage == null || passage.isBlank())
            return;

        int pageIndex = page - 1;
        String pageText = pageTexts.get(pageIndex);
        TextFlow flow = pageFlows.get(pageIndex);
        Pane overlay = pageOverlays.get(pageIndex);
        if (pageText == null || flow == null || overlay == null)
            return;

        String needle = passage.strip();
        int start = pageText.indexOf(needle);
        if (start < 0) {
            needle = needle.substring(0, Math.min(40, needle.length()));
            start = pageText.indexOf(needle);
        }
        if (start < 0)
            return;

        Node shape = TxtTextLayout.shapeFor(
                flow, start, start + needle.length(),
                TxtHighlight.SEARCH_STYLE_CLASS);
        overlay.getChildren().add(shape);
        scrollToShape(shape);
    }

    private void centerPage(Node pageNode) {
        if (pageNode == null) {
            return;
        }

        double viewportWidth = scrollPane.getViewportBounds().getWidth();
        double viewportHeight = scrollPane.getViewportBounds().getHeight();
        double contentWidth = pagesHost.getWidth();
        double contentHeight = pagesHost.getHeight();

        double pageX = pageNode.getBoundsInParent().getMinX();
        double pageY = pageNode.getBoundsInParent().getMinY();
        double pageWidth = pageNode.getBoundsInParent().getWidth();
        double pageHeight = pageNode.getBoundsInParent().getHeight();

        double targetX = pageX + pageWidth / 2.0 - viewportWidth / 2.0;
        double targetY = pageY + pageHeight / 2.0 - viewportHeight / 2.0;

        double maxX = Math.max(0, contentWidth - viewportWidth);
        double maxY = Math.max(0, contentHeight - viewportHeight);

        double horizontal = maxX == 0 ? 0 : targetX / maxX;
        double vertical = maxY == 0 ? 0 : targetY / maxY;

        scrollPane.setHvalue(clamp(horizontal));
        scrollPane.setVvalue(clamp(vertical));
    }

    private double clamp(double value) {
        return Math.max(0, Math.min(1, value));
    }

    private void updateVisiblePage() {
        if (pagesHost.getChildren().isEmpty()) {
            return;
        }

        double viewportHeight = scrollPane.getViewportBounds().getHeight();
        double scrollableHeight = Math.max(
                0,
                pagesHost.getHeight() - viewportHeight);

        double viewportCenter = scrollPane.getVvalue()
                * scrollableHeight
                + viewportHeight / 2.0;

        int closest = 0;
        double best = Double.MAX_VALUE;

        for (int i = 0; i < pagesHost.getChildren().size(); i++) {
            Node node = pagesHost.getChildren().get(i);

            double center = node.getBoundsInParent().getMinY()
                    + node.getBoundsInParent().getHeight() / 2.0;

            double distance = Math.abs(center - viewportCenter);

            if (distance < best) {
                best = distance;
                closest = i;
            }
        }

        int page = closest + 1;

        if (page != currentVisiblePage) {
            currentVisiblePage = page;
            onVisiblePageChanged.accept(page);
        }
    }

    public void setZoom(double zoom) {
        this.zoom = Math.max(0.30, Math.min(1.5, zoom));

        updatePageSizes();

        Platform.runLater(() -> {
            updatePageSizes();

            pendingQuotationPage = -1;
            hideQuotationPopup();
            refreshAllQuotationHighlights();

            updateVisiblePage();
        });
    }

    public int getPageCount() {
        return pageStarts.size();
    }

    public void setOnVisiblePageChanged(Consumer<Integer> handler) {
        onVisiblePageChanged = handler == null
                ? ignored -> {
                }
                : handler;
    }

    public void setOnQuotationRequested(
            BiFunction<String, String, Integer> handler) {

        onQuotationRequested = handler == null
                ? (quotationText, location) -> null
                : handler;
    }

    private void applyQuotationHighlights(
            int pageIndex,
            TextFlow flow,
            Pane overlay) {

        overlay.getChildren().clear();

        for (Map.Entry<Integer, Integer> entry : quotationPageById.entrySet()) {
            if (entry.getValue() != pageIndex) {
                continue;
            }

            TxtHighlight.Range range = quotationRangeById.get(entry.getKey());

            if (range != null) {
                overlay.getChildren().add(
                        TxtTextLayout.shapeFor(
                                flow,
                                range.start(),
                                range.end(),
                                TxtHighlight.QUOTATION_STYLE_CLASS));
            }
        }
    }

    private void refreshAllQuotationHighlights() {
        for (Map.Entry<Integer, Pane> entry : pageOverlays.entrySet()) {
            int pageIndex = entry.getKey();
            TextFlow flow = pageFlows.get(pageIndex);

            if (flow != null) {
                applyQuotationHighlights(
                        pageIndex,
                        flow,
                        entry.getValue());
            }
        }
    }

    public void removeQuotationHighlight(int quotationId) {
        Integer page = quotationPageById.remove(quotationId);
        quotationRangeById.remove(quotationId);

        if (page != null) {
            TextFlow flow = pageFlows.get(page);
            Pane overlay = pageOverlays.get(page);

            if (flow != null && overlay != null) {
                applyQuotationHighlights(
                        page,
                        flow,
                        overlay);
            }
        }
    }

    private void cancelPendingSelection() {
        if (pendingQuotationPage < 0) {
            return;
        }

        TextFlow flow = pageFlows.get(pendingQuotationPage);
        Pane overlay = pageOverlays.get(pendingQuotationPage);

        if (flow != null && overlay != null) {
            applyQuotationHighlights(
                    pendingQuotationPage,
                    flow,
                    overlay);
        }

        pendingQuotationPage = -1;
    }

    private void beginSelection(
            int pageIndex,
            TextFlow flow,
            MouseEvent event) {

        hideQuotationPopup();
        cancelPendingSelection();

        scrollPane.setPannable(false);
        event.consume();

        activeSelectionPage = pageIndex;
        selectionStartIndex = charIndexAt(flow, event);
    }

    private void updateSelection(
            int pageIndex,
            TextFlow flow,
            Pane overlay,
            MouseEvent event) {

        if (activeSelectionPage != pageIndex
                || selectionStartIndex < 0) {
            return;
        }

        event.consume();

        int current = charIndexAt(flow, event);

        applyQuotationHighlights(
                pageIndex,
                flow,
                overlay);

        if (current != selectionStartIndex) {
            overlay.getChildren().add(
                    TxtTextLayout.shapeFor(
                            flow,
                            selectionStartIndex,
                            current,
                            LIVE_SELECTION_STYLE_CLASS));
        }
    }

    private void endSelection(
            int pageIndex,
            TextFlow flow,
            String pageText,
            Pane overlay,
            MouseEvent event) {

        scrollPane.setPannable(true);

        if (activeSelectionPage != pageIndex
                || selectionStartIndex < 0) {
            return;
        }

        event.consume();

        int current = charIndexAt(flow, event);

        int start = Math.min(
                selectionStartIndex,
                current);

        int end = Math.max(
                selectionStartIndex,
                current);

        activeSelectionPage = -1;
        selectionStartIndex = -1;

        if (end <= start) {
            applyQuotationHighlights(
                    pageIndex,
                    flow,
                    overlay);
            return;
        }

        start = Math.max(0, Math.min(start, pageText.length()));
        end = Math.max(0, Math.min(end, pageText.length()));

        if (end <= start) {
            applyQuotationHighlights(
                    pageIndex,
                    flow,
                    overlay);
            return;
        }

        String selectedText = pageText
                .substring(start, end)
                .strip();

        if (selectedText.isEmpty()) {
            applyQuotationHighlights(
                    pageIndex,
                    flow,
                    overlay);
            return;
        }

        pendingQuotationPage = pageIndex;

        String rawLocation = "page:"
                + (pageIndex + 1)
                + ";offset:"
                + start
                + "-"
                + end;

        showQuotationPopup(
                event.getScreenX(),
                event.getScreenY(),
                selectedText,
                rawLocation,
                pageIndex,
                flow,
                overlay,
                start,
                end);
    }

    private int charIndexAt(
            TextFlow flow,
            MouseEvent event) {

        int index = flow.hitTest(
                new Point2D(
                        event.getX(),
                        event.getY()))
                .getCharIndex();

        if (index < 0) {
            return 0;
        }

        String pageText = pageTexts.get(
                activeSelectionPage >= 0
                        ? activeSelectionPage
                        : currentVisiblePage - 1);

        if (pageText != null) {
            return Math.min(index, pageText.length());
        }

        return index;
    }

    private void showQuotationPopup(
            double screenX,
            double screenY,
            String selectedText,
            String rawLocation,
            int pageIndex,
            TextFlow flow,
            Pane overlay,
            int start,
            int end) {

        quotationPopup = new QuotationSelectionPopup(type -> {
            String location = QuotationLocation.encode(
                    type,
                    rawLocation);

            Integer quotationId = onQuotationRequested.apply(
                    selectedText,
                    location);

            if (quotationId != null) {
                quotationPageById.put(
                        quotationId,
                        pageIndex);

                quotationRangeById.put(
                        quotationId,
                        new TxtHighlight.Range(
                                start,
                                end));
            }

            applyQuotationHighlights(
                    pageIndex,
                    flow,
                    overlay);

            pendingQuotationPage = -1;
            hideQuotationPopup();
        });

        quotationPopup.showAt(
                selectionOverlay,
                screenX,
                screenY);
    }

    private void hideQuotationPopup() {
        if (quotationPopup != null) {
            quotationPopup.hide();
            quotationPopup = null;
        }
    }

    public void dispose() {
        hideQuotationPopup();

        pendingQuotationPage = -1;
        activeSelectionPage = -1;
        selectionStartIndex = -1;

        quotationPageById.clear();
        quotationRangeById.clear();
        pageFlows.clear();
        pageOverlays.clear();
        pageTexts.clear();

        pagesHost.getChildren().clear();
    }

    public void highlightSearchMatches(
            List<SearchMatch> matches,
            int activeIndex) {

        clearSearchHighlights();

        if (matches == null || matches.isEmpty()) {
            return;
        }

        for (int i = 0; i < matches.size(); i++) {
            SearchMatch match = matches.get(i);

            if (match == null || match.page() == null) {
                continue;
            }

            int pageIndex = match.page() - 1;

            if (pageIndex < 0
                    || pageIndex >= pageStarts.size()) {
                continue;
            }

            TextFlow flow = pageFlows.get(pageIndex);
            Pane overlay = pageOverlays.get(pageIndex);

            if (flow == null || overlay == null) {
                continue;
            }

            int pageStart = pageStarts.get(pageIndex);

            int localStart = match.matchStart() - pageStart;
            int localEnd = match.matchEnd() - pageStart;

            if (localEnd <= 0) {
                continue;
            }

            String pageText = pageTexts.get(pageIndex);

            if (pageText == null) {
                continue;
            }

            int pageLength = pageText.length();

            localStart = Math.max(
                    0,
                    localStart);

            localEnd = Math.min(
                    pageLength,
                    localEnd);

            if (localStart >= localEnd) {
                continue;
            }

            String styleClass = i == activeIndex
                    ? TxtHighlight.ACTIVE_SEARCH_STYLE_CLASS
                    : TxtHighlight.SEARCH_STYLE_CLASS;

            Node shape = TxtTextLayout.shapeFor(flow, localStart, localEnd, styleClass);
            overlay.getChildren().add(shape);

            if (i == activeIndex) {
                scrollToShape(shape);
            }
        }
    }

    public void highlightSearchMatch(SearchMatch match) {
        clearSearchHighlights();

        if (match == null
                || match.page() == null) {
            return;
        }

        int pageIndex = match.page() - 1;

        if (pageIndex < 0
                || pageIndex >= pageStarts.size()) {
            return;
        }

        TextFlow flow = pageFlows.get(pageIndex);
        Pane overlay = pageOverlays.get(pageIndex);

        if (flow == null || overlay == null) {
            return;
        }

        int pageStart = pageStarts.get(pageIndex);

        int localStart = match.matchStart() - pageStart;
        int localEnd = match.matchEnd() - pageStart;

        String pageText = pageTexts.get(pageIndex);

        if (pageText == null) {
            return;
        }

        int pageLength = pageText.length();

        localStart = Math.max(
                0,
                localStart);

        localEnd = Math.min(
                pageLength,
                localEnd);

        if (localStart >= localEnd) {
            return;
        }

        overlay.getChildren().add(
                TxtTextLayout.shapeFor(
                        flow,
                        localStart,
                        localEnd,
                        TxtHighlight.ACTIVE_SEARCH_STYLE_CLASS));
    }

    private void scrollToShape(Node shape) {
        Platform.runLater(() -> {
            Bounds b = pagesHost.sceneToLocal(shape.localToScene(shape.getBoundsInLocal()));
            double viewportHeight = scrollPane.getViewportBounds().getHeight();
            double maxY = Math.max(0, pagesHost.getHeight() - viewportHeight);

            if (b == null || maxY == 0)
                return;

            scrollPane.setVvalue(clamp((b.getMinY() - viewportHeight / 3.0) / maxY));
        });
    }

    public void clearSearchHighlights() {
        for (Pane overlay : pageOverlays.values()) {
            overlay.getChildren().clear();
        }

        refreshAllQuotationHighlights();
    }
}
