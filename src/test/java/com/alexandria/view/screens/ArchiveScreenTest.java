package com.alexandria.view.screens;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.alexandria.model.ArchiveTermAnalysis;
import com.alexandria.model.ArchiveTextAnalysis;
import com.alexandria.service.analysis.TermAnalysisResult;
import com.alexandria.service.analysis.TextAnalysisResult;
import com.alexandria.view.components.shared.EmptyState;
import com.alexandria.view.components.shared.SearchInput;

import org.junit.BeforeClass;
import org.junit.Test;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.List;

public class ArchiveScreenTest {

    @BeforeClass
    public static void startJavaFx() {
        try {
            Platform.startup(() -> {
            });
        } catch (IllegalStateException ignored) {
        }
    }

    @Test
    public void emptyArchiveShowsMessage() {
        ArchiveScreen screen = new ArchiveScreen();
        VBox statistics = statistics(screen);
        StackPane statisticsArea = (StackPane) statistics.getChildren().get(2);

        assertEquals("No saved text analyses yet", title((EmptyState) statisticsArea.getChildren().get(0)));
    }

    @Test
    public void textAnalysisShowsCard() {
        ArchiveScreen screen = new ArchiveScreen();
        screen.setTextAnalyses(List.of(textAnalysis()));

        assertEquals(1, cards(screen).getChildren().size());
    }

    @Test
    public void termAnalysisShowsCard() {
        ArchiveScreen screen = new ArchiveScreen();
        screen.setTermAnalyses(List.of(termAnalysis()));

        HBox filters = (HBox) statistics(screen).getChildren().get(0);
        ToggleButton termButton = (ToggleButton) filters.getChildren().get(1);
        termButton.fire();

        assertEquals(1, cards(screen).getChildren().size());
    }

    @Test
    public void searchHidesCardsWithoutMatches() {
        ArchiveScreen screen = new ArchiveScreen();
        screen.setTextAnalyses(List.of(textAnalysis()));

        SearchInput searchInput = (SearchInput) statistics(screen).getChildren().get(1);
        searchInput.textProperty().set("unknown");

        StackPane statisticsArea = (StackPane) statistics(screen).getChildren().get(2);
        assertEquals("Nothing found", title((EmptyState) statisticsArea.getChildren().get(0)));
    }

    @Test
    public void signInMessageShowsText() {
        ArchiveScreen screen = new ArchiveScreen();
        screen.showSignInMessage();

        EmptyState signInMessage = (EmptyState) layout(screen).getCenter();

        assertEquals("Sign in to view your archive", title(signInMessage));
    }

    private BorderPane layout(ArchiveScreen screen) {
        return (BorderPane) screen.getChildren().get(0);
    }

    private VBox statistics(ArchiveScreen screen) {
        VBox body = (VBox) layout(screen).getCenter();
        StackPane contentArea = (StackPane) body.getChildren().get(0);
        return (VBox) contentArea.getChildren().get(0);
    }

    private FlowPane cards(ArchiveScreen screen) {
        VBox statistics = statistics(screen);
        StackPane statisticsArea = (StackPane) statistics.getChildren().get(2);
        ScrollPane scroll = (ScrollPane) statisticsArea.getChildren().get(0);
        return (FlowPane) scroll.getContent();
    }

    private String title(EmptyState emptyState) {
        return ((Label) emptyState.getChildren().get(0)).getText();
    }

    private ArchiveTextAnalysis textAnalysis() {
        TextAnalysisResult result = new TextAnalysisResult(10, 8, 2, 1, List.of(), List.of());
        return new ArchiveTextAnalysis(1, 1, "Climate", "climate.pdf", null, result);
    }

    private ArchiveTermAnalysis termAnalysis() {
        TermAnalysisResult result = new TermAnalysisResult("climate", 2, 200.0, 2, 1, List.of());
        return new ArchiveTermAnalysis(1, 1, "Climate", "climate.pdf", "climate", null, result);
    }
}
