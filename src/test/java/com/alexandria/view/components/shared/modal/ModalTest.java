package com.alexandria.view.components.shared.modal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import javafx.application.Platform;
import javafx.scene.control.Label;

public class ModalTest {

    @BeforeClass
    public static void startJavaFx() {
        try {
            Platform.startup(() -> {
            });
        } catch (IllegalStateException ignored) {
        }
    }

    @Test
    public void modalIsHiddenByDefault() {
        Modal modal = new Modal();

        assertFalse(modal.isShowing());
    }

    @Test
    public void showMakesModalVisible() {
        Modal modal = new Modal();

        modal.show();

        assertTrue(modal.isShowing());
    }

    @Test
    public void hideMakesModalHidden() {
        Modal modal = new Modal();
        modal.show();

        modal.hide();

        assertFalse(modal.isShowing());
    }

    @Test
    public void showWithContentStoresContent() {
        Modal modal = new Modal();
        Label content = new Label("Content");

        modal.show(content);

        assertEquals(content, modal.getContent());
    }
}
