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

    private final ScrollPane scrollPane = new ScrollPane();
    private final VBox pagesHost = new VBox(24);

    private String content = "";
    private List<Integer> pageStarts = List.of();
    private int currentVisiblePage = 1;

    private Consumer<Integer> onVisiblePageChanged = ignored -> {
    };

    public TextDocumentRenderer(String content) {
        pagesHost.setAlignment(Pos.TOP_CENTER);
        pagesHost.getStyleClass().add("document-pages");

        scrollPane.setContent(pagesHost);
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);
        scrollPane.getStyleClass().add("viewer-scroll");

        scrollPane.vvalueProperty().addListener((obs, o, n) -> updateVisiblePage());

        setText(content);
    }

    private void setText(String content) {
        this.content = content == null ? "" : content;
        pageStarts = paginate(this.content, CHARS_PER_PAGE);
        if (pageStarts.isEmpty())
            pageStarts = List.of(0);

        rebuild();
        Platform.runLater(this::updateVisiblePage);
    }

    private void rebuild() {
        pagesHost.getChildren().clear();

        for (int i = 0; i < pageStarts.size(); i++) {
            int start = pageStarts.get(i);
            int end = i + 1 < pageStarts.size() ? pageStarts.get(i + 1) : content.length();
            String pageText = content.substring(start, Math.min(end, content.length()));

            TextFlow flow = new TextFlow(new Text(pageText));
            flow.getStyleClass().add("document-page-text");

            VBox page = new VBox(flow);
            page.getStyleClass().add("document-page");
            pagesHost.getChildren().add(page);
        }
    }

    private static List<Integer> paginate(String content, int charsPerPage) {
        List<Integer> offsets = new ArrayList<>();
        if (content.isEmpty())
            return offsets;

        offsets.add(0);
        int pos = 0;
        while (pos + charsPerPage < content.length()) {
            int candidate = pos + charsPerPage;
            int breakPoint = content.lastIndexOf("\n\n", candidate);
            if (breakPoint <= pos)
                breakPoint = content.lastIndexOf(' ', candidate);
            if (breakPoint <= pos)
                breakPoint = candidate;
            pos = breakPoint;
            offsets.add(pos);
        }
        return offsets;
    }

    public Node getNode() {
        return scrollPane;
    }

    public void goToPage(Integer page, Integer paragraph) {
        if (page == null || pagesHost.getChildren().isEmpty())
            return;

        int safe = Math.max(0, Math.min(page - 1, pagesHost.getChildren().size() - 1));
        Node pageNode = pagesHost.getChildren().get(safe);

        Platform.runLater(() -> {
            double total = Math.max(1, pagesHost.getHeight() - scrollPane.getViewportBounds().getHeight());
            double position = pageNode.getBoundsInParent().getMinY() / total;
            scrollPane.setVvalue(Math.max(0, Math.min(1, position)));
        });
    }

    private void updateVisiblePage() {
        if (pagesHost.getChildren().isEmpty())
            return;

        double viewportCenter = scrollPane.getVvalue()
                * Math.max(0, pagesHost.getHeight() - scrollPane.getViewportBounds().getHeight())
                + scrollPane.getViewportBounds().getHeight() / 2;

        int closest = 0;
        double best = Double.MAX_VALUE;

        for (int i = 0; i < pagesHost.getChildren().size(); i++) {
            Node node = pagesHost.getChildren().get(i);
            double center = node.getBoundsInParent().getMinY() + node.getBoundsInParent().getHeight() / 2;
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
        double safe = Math.max(0.75, Math.min(1.5, zoom));
        pagesHost.setScaleX(safe);
        pagesHost.setScaleY(safe);
    }

    public int getPageCount() {
        return pageStarts.size();
    }

    public void setOnVisiblePageChanged(Consumer<Integer> handler) {
        onVisiblePageChanged = handler == null ? ignored -> {
        } : handler;
    }

    public void dispose() {
        pagesHost.getChildren().clear();
    }
}
