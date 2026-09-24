package com.alexandria.view.components.shared;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

import javafx.application.Platform;
import javafx.scene.control.Label;

public class EmptyStateTest {

    @BeforeClass
    public static void startJavaFx() {
        try {
            Platform.startup(() -> {
            });
        } catch (IllegalStateException ignored) {
        }
    }

    @Test
    public void showsTitleAndSubtitle() {
        EmptyState emptyState = new EmptyState("No document open", "Open a document to begin.");

        assertEquals("No document open", ((Label) emptyState.getChildren().get(0)).getText());
        assertEquals("Open a document to begin.", ((Label) emptyState.getChildren().get(1)).getText());
    }

    @Test
    public void showsOnlyTitleWithoutSubtitle() {
        EmptyState emptyState = new EmptyState("No saved analyses yet", "");

        assertEquals(1, emptyState.getChildren().size());
    }
}
