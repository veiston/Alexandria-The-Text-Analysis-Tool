package com.alexandria.view.router;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import org.junit.BeforeClass;
import org.junit.Test;

import javafx.application.Platform;
import javafx.scene.Node;

public class RouteTest {

    @BeforeClass
    public static void startJavaFx() {
        try {
            Platform.startup(() -> {
            });
        } catch (IllegalStateException ignored) {
        }
    }

    @Test
    public void fromIdReturnsLibraryRoute() {
        assertEquals(Route.LIBRARY, Route.fromId("library"));
    }

    @Test
    public void fromIdThrowsErrorForUnknownId() {
        try {
            Route.fromId("unknown");
            fail("Unknown route id should throw an error.");
        } catch (IllegalArgumentException ignored) {
        }
    }

    @Test
    public void createScreenReturnsSameScreenForSameRoute() {
        Node firstScreen = Route.LIBRARY.createScreen();
        Node secondScreen = Route.LIBRARY.createScreen();

        assertSame(firstScreen, secondScreen);
    }
}
