package com.alexandria.view.components.analyse_screen.input_term_analyse;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

public class TrackedWordsList extends VBox {
    private final Label headingLabel = new Label("Tracked Words");
    private final Label countBadge = new Label("0");
    private final FontIcon toggleIcon = new FontIcon(FontAwesomeSolid.CHEVRON_UP);
    private final Button toggleButton = new Button();

    private final VBox rowsHost = new VBox(8);
    private final Label emptyLabel = new Label("Search a term to start tracking it.");

    private final Map<String, Row> rows = new LinkedHashMap<>();

    private boolean expanded = true;
    private String activeTerm;

    private Consumer<String> onInfo = word -> {
    };
    private Consumer<String> onRemove = word -> {
    };
    private Consumer<String> onGoTo = word -> {
    };

    public TrackedWordsList() {
        getStyleClass().add("tracked-words-panel");
        setSpacing(8);
        setPadding(new Insets(8, 0, 0, 0));

        headingLabel.getStyleClass().add("heading-md");
        countBadge.getStyleClass().add("text-muted");

        toggleIcon.getStyleClass().add("text-muted");
        toggleButton.setGraphic(toggleIcon);
        toggleButton.getStyleClass().add("icon-button");
        toggleButton.setOnAction(e -> setExpanded(!expanded));

        HBox headerLeft = new HBox(8, headingLabel, countBadge);
        headerLeft.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(headerLeft, Priority.ALWAYS);

        HBox header = new HBox(headerLeft, toggleButton);
        header.setAlignment(Pos.CENTER_LEFT);
        header.getStyleClass().add("tracked-words-header");

        emptyLabel.getStyleClass().add("text-muted");

        getChildren().addAll(header, rowsHost);
        showEmptyIfNeeded();
    }

    public void addOrUpdate(String word, int count) {
        if (word == null || word.isBlank()) {
            return;
        }

        Row row = rows.get(word);

        if (row == null) {
            row = new Row(word);
            rows.put(word, row);
            rowsHost.getChildren().add(row.container);
        }

        row.count = Math.max(0, count);

        refreshBars();
        showEmptyIfNeeded();
    }

    public void remove(String word) {
        Row row = rows.remove(word);

        if (row == null) {
            return;
        }

        rowsHost.getChildren().remove(row.container);

        if (word.equals(activeTerm)) {
            activeTerm = null;
        }

        refreshBars();
        showEmptyIfNeeded();
    }

    public void setActiveTerm(String word) {
        if (activeTerm != null) {
            Row previous = rows.get(activeTerm);
        }

        activeTerm = word;

        if (activeTerm != null) {
            Row current = rows.get(activeTerm);
        }
    }

    public void setOnInfo(Consumer<String> handler) {
        onInfo = handler == null ? word -> {
        } : handler;
    }

    public void setOnRemove(Consumer<String> handler) {
        onRemove = handler == null ? word -> {
        } : handler;
    }

    public void setOnGoTo(Consumer<String> handler) {
        onGoTo = handler == null ? word -> {
        } : handler;
    }

    public void reset() {
        rows.clear();
        rowsHost.getChildren().clear();
        activeTerm = null;
        showEmptyIfNeeded();
    }

    private void setExpanded(boolean value) {
        expanded = value;

        rowsHost.setManaged(expanded);
        rowsHost.setVisible(expanded);

        toggleIcon.setIconCode(expanded
                ? FontAwesomeSolid.CHEVRON_UP
                : FontAwesomeSolid.CHEVRON_DOWN);
    }

    private void showEmptyIfNeeded() {
        countBadge.setText(String.valueOf(rows.size()));

        boolean isEmpty = rows.isEmpty();

        if (isEmpty && !rowsHost.getChildren().contains(emptyLabel)) {
            rowsHost.getChildren().setAll(emptyLabel);
        } else if (!isEmpty) {
            rowsHost.getChildren().remove(emptyLabel);
        }
    }

    private void refreshBars() {
        int maxCount = rows.values().stream()
                .mapToInt(row -> row.count)
                .max()
                .orElse(1);

        for (Row row : rows.values()) {
            row.updateBar(maxCount);
        }
    }

    private final class Row {
        private final String word;
        private final HBox container;
        private final Region bar;
        private final Label countLabel;

        private int count;

        private Row(String word) {
            this.word = word;

            Label termLabel = new Label(word);
            termLabel.getStyleClass().add("term-frequency-label");
            termLabel.setMinWidth(80);
            termLabel.setPrefWidth(80);

            Region track = new Region();
            track.getStyleClass().add("track");

            bar = new Region();
            bar.getStyleClass().add("bar");

            StackPane frequencyBar = new StackPane(track, bar);
            frequencyBar.getStyleClass().add("frequency-bar");
            frequencyBar.setMinWidth(0);
            frequencyBar.setPrefHeight(7);
            frequencyBar.setMaxHeight(7);

            StackPane.setAlignment(track, Pos.CENTER_LEFT);
            StackPane.setAlignment(bar, Pos.CENTER_LEFT);
            bar.setMaxWidth(Region.USE_PREF_SIZE);

            HBox.setHgrow(frequencyBar, Priority.ALWAYS);

            countLabel = new Label("0");
            countLabel.getStyleClass().add("text-muted");
            countLabel.setMinWidth(28);

            Button goToButton = createIconButton(FontAwesomeSolid.LOCATION_ARROW, "tracked-word-goto");
            goToButton.setOnAction(e -> onGoTo.accept(word));

            Button removeButton = createIconButton(FontAwesomeSolid.TRASH, "tracked-word-remove");
            removeButton.setOnAction(e -> onRemove.accept(word));

            HBox clickableArea = new HBox(10, termLabel, frequencyBar, countLabel);
            clickableArea.setAlignment(Pos.CENTER_LEFT);
            HBox.setHgrow(clickableArea, Priority.ALWAYS);
            clickableArea.setOnMouseClicked(e -> onInfo.accept(word));

            container = new HBox(8, clickableArea, goToButton, removeButton);
            container.setAlignment(Pos.CENTER_LEFT);
            container.getStyleClass().add("tracked-word-row");
        }

        private void updateBar(int maxCount) {
            countLabel.setText(String.valueOf(count));

            double ratio = maxCount <= 0 ? 0 : (double) count / maxCount;
            bar.prefWidthProperty().unbind();
            bar.prefWidthProperty().bind(
                    ((StackPane) bar.getParent()).widthProperty().multiply(ratio));
        }

        private Button createIconButton(FontAwesomeSolid icon, String styleClass) {
            FontIcon fontIcon = new FontIcon(icon);
            fontIcon.getStyleClass().add("text-muted");

            Button button = new Button();
            button.setGraphic(fontIcon);
            button.getStyleClass().addAll("icon-button", styleClass);

            return button;
        }
    }
}