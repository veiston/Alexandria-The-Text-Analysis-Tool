package com.alexandria.view.screens;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.BeforeClass;
import org.junit.Test;

import com.alexandria.model.FileType;
import com.alexandria.model.Text;
import com.alexandria.view.components.shared.Card;
import com.alexandria.view.components.shared.EmptyState;
import com.alexandria.view.components.shared.SearchInput;
import com.alexandria.view.components.shared.modal.Modal;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class LibraryScreenTest {

    @BeforeClass
    public static void startJavaFx() {
        try {
            Platform.startup(() -> {
            });
        } catch (IllegalStateException ignored) {
        }
    }

    @Test
    public void emptyLibraryShowsNewProjectCard() {
        LibraryScreen screen = new LibraryScreen();
        assertEquals(1, cards(screen).getChildren().size());
    }

    @Test
    public void libraryWithTextsShowsCards() {
        LibraryScreen screen = new LibraryScreen();
        screen.setTexts(List.of(sampleText(1, "Meditations")));

        FlowPane cards = cards(screen);
        assertEquals(2, cards.getChildren().size());
        assertTrue(cards.getChildren().get(1) instanceof Card);
    }

    @Test
    public void searchHidesNonMatchingTexts() {
        LibraryScreen screen = new LibraryScreen();
        screen.setTexts(List.of(sampleText(1, "Meditations")));

        searchInput(screen).textProperty().set("unknown");
        assertEquals("Nothing found", title((EmptyState) contentArea(screen).getChildren().get(0)));
    }

    @Test
    public void searchMatchesTitle() {
        LibraryScreen screen = new LibraryScreen();
        screen.setTexts(List.of(sampleText(1, "Meditations"), sampleText(2, "Republic")));

        searchInput(screen).textProperty().set("Meditations");
        assertEquals(1, cards(screen).getChildren().size());
    }

    @Test
    public void signInMessageShowsEmptyState() {
        LibraryScreen screen = new LibraryScreen();
        screen.showSignInMessage();

        assertEquals("Sign in to view your library", title((EmptyState) layout(screen).getCenter()));
    }

    @Test
    public void newProjectCardTriggersCallback() {
        LibraryScreen screen = new LibraryScreen();
        AtomicBoolean called = new AtomicBoolean(false);
        screen.setOnNewProject(() -> {
            called.set(true);
        });

        VBox newProjectCard = (VBox) cards(screen).getChildren().get(0);
        newProjectCard.getOnMouseClicked().handle(null);

        assertTrue(called.get());
    }

    @Test
    public void cardProvidesOpenWithAction() {
        LibraryScreen screen = new LibraryScreen();
        screen.setTexts(List.of(sampleText(1, "Meditations")));

        Card card = (Card) cards(screen).getChildren().get(1);
        assertEquals("Open with", card.getActionButton().getText());
    }

    @Test
    public void comparisonModalSendsTheFirstAndSecondTexts() {
        Text firstText = sampleText(1, "Meditations");
        Text secondText = sampleText(2, "Republic");
        LibraryScreen screen = new LibraryScreen();
        AtomicReference<Text> selectedFirst = new AtomicReference<>();
        AtomicReference<Text> selectedSecond = new AtomicReference<>();
        screen.setTexts(List.of(firstText, secondText));
        screen.setOnCompare((first, second) -> {
            selectedFirst.set(first);
            selectedSecond.set(second);
        });

        Card firstCard = (Card) cards(screen).getChildren().get(1);
        firstCard.getActionButton().fire();

        VBox openModal = (VBox) modal(screen).getContent();
        VBox actions = (VBox) openModal.getChildren().get(1);
        Button compareButton = (Button) actions.getChildren().get(1);
        compareButton.fire();

        VBox comparisonModal = (VBox) modal(screen).getContent();
        VBox selection = (VBox) comparisonModal.getChildren().get(1);
        ScrollPane scroll = (ScrollPane) selection.getChildren().get(0);
        FlowPane comparisonCards = (FlowPane) scroll.getContent();
        Card secondCard = (Card) comparisonCards.getChildren().get(0);
        secondCard.getActionButton().fire();

        assertEquals(firstText, selectedFirst.get());
        assertEquals(secondText, selectedSecond.get());
    }

    private BorderPane layout(LibraryScreen screen) {
        return (BorderPane) screen.getChildren().get(0);
    }

    private StackPane contentArea(LibraryScreen screen) {
        VBox body = (VBox) layout(screen).getCenter();
        StackPane holder = (StackPane) body.getChildren().get(0);
        VBox searchAndContent = (VBox) holder.getChildren().get(0);
        return (StackPane) searchAndContent.getChildren().get(1);
    }

    private FlowPane cards(LibraryScreen screen) {
        ScrollPane scroll = (ScrollPane) contentArea(screen).getChildren().get(0);
        return (FlowPane) scroll.getContent();
    }

    private SearchInput searchInput(LibraryScreen screen) {
        VBox body = (VBox) layout(screen).getCenter();
        StackPane holder = (StackPane) body.getChildren().get(0);
        VBox searchAndContent = (VBox) holder.getChildren().get(0);
        return (SearchInput) searchAndContent.getChildren().get(0);
    }

    private Modal modal(LibraryScreen screen) {
        return (Modal) screen.getChildren().get(1);
    }

    private String title(EmptyState emptyState) {
        return ((Label) emptyState.getChildren().get(0)).getText();
    }

    private Text sampleText(int id, String title) {
        return new Text(id, 1, title, "doc.pdf", null, FileType.PDF, "content", LocalDateTime.now());
    }
}
