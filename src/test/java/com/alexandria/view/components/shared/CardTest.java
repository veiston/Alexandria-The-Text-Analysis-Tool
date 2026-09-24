package com.alexandria.view.components.shared;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

import javafx.application.Platform;
import javafx.scene.control.Label;

public class CardTest {

    @BeforeClass
    public static void startJavaFx() {
        try {
            Platform.startup(() -> {
            });
        } catch (IllegalStateException ignored) {
        }
    }

    @Test
    public void cardShowsCommonContent() {
        Card card = new Card("Project");
        card.setTypeText("Text analysis");
        card.setSourceText("source.pdf");
        card.setFooterText("Saved today");
        card.setActionText("Open");

        Label term = new Label("Term: research");
        card.setExtraContent(term);

        assertEquals("Project", ((Label) ((javafx.scene.layout.VBox) card.getChildren().get(1))
                .getChildren().get(0)).getText());
        assertEquals(term, ((javafx.scene.layout.VBox) card.getChildren().get(1))
                .getChildren().get(1));
        assertEquals("Open", card.getActionButton().getText());
    }
}
