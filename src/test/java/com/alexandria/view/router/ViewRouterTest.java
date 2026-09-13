package com.alexandria.view.router;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import javafx.application.Platform;

public class ViewRouterTest {

    @BeforeClass
    public static void startJavaFx() {
        try {
            Platform.startup(() -> {
            });
        } catch (IllegalStateException ignored) {
        }
    }

    @Test
    public void navigateToSetsCurrentRouteAndScreen() {
        ViewRouter router = new ViewRouter();

        router.navigateTo(Route.LIBRARY);

        assertEquals(Route.LIBRARY, router.getCurrentRoute());
        assertEquals(1, router.getChildren().size());
    }

    @Test
    public void clearRemovesCurrentRouteAndScreen() {
        ViewRouter router = new ViewRouter();
        router.navigateTo(Route.LIBRARY);

        router.clear();

        assertNull(router.getCurrentRoute());
        assertTrue(router.getChildren().isEmpty());
    }
}
