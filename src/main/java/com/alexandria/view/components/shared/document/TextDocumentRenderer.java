package com.alexandria.view.components.shared.document;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TextDocumentRenderer {

    private static final int CHARS_PER_PAGE = 1800;
    private static final double BASE_PAGE_WIDTH = 620.0;

    private final ScrollPane scrollPane = new ScrollPane();
    private final VBox pagesHost = new VBox(24);

    private String content = "";
    private List<Integer> pageStarts = List.of();

    private int currentVisiblePage = 1;
    private double zoom = 1.0;

    private Consumer<Integer> onVisiblePageChanged = ignored -> {
    };

    public TextDocumentRenderer(String content) {
        pagesHost.setAlignment(Pos.TOP_CENTER);
        pagesHost.getStyleClass().add("document-pages");

        scrollPane.setContent(pagesHost);

        /*
         * Do not force the document to fit the viewport.
         * This allows horizontal scrolling when zoomed.
         */
        scrollPane.setFitToWidth(false);
        scrollPane.setFitToHeight(false);
        scrollPane.setPannable(true);
        scrollPane.getStyleClass().add("viewer-scroll");

        scrollPane.vvalueProperty().addListener(
                (obs, oldValue, newValue) -> updateVisiblePage());

        setText(content);
    }

    private void setText(String content) {
        this.content = content == null ? "" : content;

        pageStarts = paginate(
                this.content,
                CHARS_PER_PAGE);

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

        for (int i = 0; i < pageStarts.size(); i++) {
            int start = pageStarts.get(i);

            int end = i + 1 < pageStarts.size()
                    ? pageStarts.get(i + 1)
                    : content.length();

            String pageText = content.substring(
                    start,
                    Math.min(end, content.length()));

            TextFlow flow = new TextFlow(
                    new Text(pageText));

            flow.getStyleClass().add(
                    "document-page-text");

            VBox page = new VBox(flow);
            page.getStyleClass().add(
                    "document-page");

            pagesHost.getChildren().add(page);
        }
    }

    private static List<Integer> paginate(
            String content,
            int charsPerPage) {

        List<Integer> offsets = new ArrayList<>();

        if (content.isEmpty()) {
            return offsets;
        }

        offsets.add(0);

        int pos = 0;

        while (pos + charsPerPage < content.length()) {
            int candidate = pos + charsPerPage;

            int breakPoint =
                    content.lastIndexOf("\n\n", candidate);

            if (breakPoint <= pos) {
                breakPoint =
                        content.lastIndexOf(' ', candidate);
            }

            if (breakPoint <= pos) {
                breakPoint = candidate;
            }

            pos = breakPoint;
            offsets.add(pos);
        }

        return offsets;
    }

    private void updatePageSizes() {
        double pageWidth =
                BASE_PAGE_WIDTH * zoom;

        /*
         * Give the host enough width for the zoomed pages.
         * The viewport can still be wider than this when zoomed out.
         */
        double viewportWidth =
                scrollPane.getViewportBounds().getWidth();

        pagesHost.setPrefWidth(
                Math.max(pageWidth, viewportWidth));

        /*
         * Resize the actual pages rather than scaling the entire
         * pagesHost. This keeps ScrollPane's layout bounds correct.
         */
        for (Node node : pagesHost.getChildren()) {
            if (node instanceof VBox page) {
                page.setPrefWidth(pageWidth);
                page.setMinWidth(pageWidth);
                page.setMaxWidth(pageWidth);
            }
        }
    }

    private void resetScrollPosition() {
        /*
         * Always start at the actual beginning of the document.
         */
        scrollPane.setVvalue(0);
        scrollPane.setHvalue(0);

        /*
         * If the document is narrower than the viewport, the pagesHost
         * centers the pages through its alignment.
         */
        Platform.runLater(() -> {
            scrollPane.setVvalue(0);
            scrollPane.setHvalue(0);
        });
    }

    public Node getNode() {
        return scrollPane;
    }

    public void goToPage(
            Integer page,
            Integer paragraph) {

        if (page == null
                || pagesHost.getChildren().isEmpty()) {
            return;
        }

        int safe = Math.max(
                0,
                Math.min(
                        page - 1,
                        pagesHost.getChildren().size() - 1));

        Node pageNode =
                pagesHost.getChildren().get(safe);

        Platform.runLater(() -> {
            updatePageSizes();
            centerPage(pageNode);
        });
    }

    private void centerPage(Node pageNode) {
        if (pageNode == null) {
            return;
        }

        double viewportWidth =
                scrollPane.getViewportBounds().getWidth();

        double viewportHeight =
                scrollPane.getViewportBounds().getHeight();

        double contentWidth =
                pagesHost.getWidth();

        double contentHeight =
                pagesHost.getHeight();

        double pageX =
                pageNode.getBoundsInParent().getMinX();

        double pageY =
                pageNode.getBoundsInParent().getMinY();

        double pageWidth =
                pageNode.getBoundsInParent().getWidth();

        double pageHeight =
                pageNode.getBoundsInParent().getHeight();

        double targetX =
                pageX
                        + pageWidth / 2.0
                        - viewportWidth / 2.0;

        double targetY =
                pageY
                        + pageHeight / 2.0
                        - viewportHeight / 2.0;

        double maxX =
                Math.max(
                        0,
                        contentWidth - viewportWidth);

        double maxY =
                Math.max(
                        0,
                        contentHeight - viewportHeight);

        double horizontal =
                maxX == 0
                        ? 0
                        : targetX / maxX;

        double vertical =
                maxY == 0
                        ? 0
                        : targetY / maxY;

        scrollPane.setHvalue(
                clamp(horizontal));

        scrollPane.setVvalue(
                clamp(vertical));
    }

    private double clamp(double value) {
        return Math.max(
                0,
                Math.min(1, value));
    }

    private void updateVisiblePage() {
        if (pagesHost.getChildren().isEmpty()) {
            return;
        }

        double viewportHeight =
                scrollPane.getViewportBounds().getHeight();

        double scrollableHeight =
                Math.max(
                        0,
                        pagesHost.getHeight()
                                - viewportHeight);

        double viewportCenter =
                scrollPane.getVvalue()
                        * scrollableHeight
                        + viewportHeight / 2.0;

        int closest = 0;
        double best = Double.MAX_VALUE;

        for (int i = 0;
                i < pagesHost.getChildren().size();
                i++) {

            Node node =
                    pagesHost.getChildren().get(i);

            double center =
                    node.getBoundsInParent().getMinY()
                            + node.getBoundsInParent().getHeight()
                            / 2.0;

            double distance =
                    Math.abs(
                            center - viewportCenter);

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
        this.zoom = Math.max(
                0.75,
                Math.min(1.5, zoom));

        updatePageSizes();

        Platform.runLater(() -> {
            updatePageSizes();
            updateVisiblePage();
        });
    }

    public int getPageCount() {
        return pageStarts.size();
    }

    public void setOnVisiblePageChanged(
            Consumer<Integer> handler) {

        onVisiblePageChanged =
                handler == null
                        ? ignored -> {
                        }
                        : handler;
    }

    public void dispose() {
        pagesHost.getChildren().clear();
    }
}

