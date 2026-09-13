package com.alexandria.view.components.shared.toggle;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

import javafx.application.Platform;

public class ToggleTest {

    @BeforeClass
    public static void startJavaFx() {
        try {
            Platform.startup(() -> {
            });
        } catch (IllegalStateException ignored) {
        }
    }

    @Test
    public void firstOptionIsSelectedByDefault() {
        Toggle toggle = new Toggle("Log In", "Create Account");

        assertEquals(0, toggle.getSelectedIndex());
    }

    @Test
    public void setSelectedIndexChangesSelectedOption() {
        Toggle toggle = new Toggle("Log In", "Create Account");

        toggle.setSelectedIndex(1);

        assertEquals(1, toggle.getSelectedIndex());
    }
}
