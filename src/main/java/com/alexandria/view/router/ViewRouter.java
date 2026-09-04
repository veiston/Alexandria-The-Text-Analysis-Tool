package com.alexandria.view.router;

import javafx.scene.Node;
import javafx.scene.layout.StackPane;

public class ViewRouter extends StackPane {

    private Route currentRoute;

    public void navigateTo(Route route) {
        if (route == null) {
            return;
        }

        if (route == currentRoute) {
            return;
        }

        currentRoute = route;

        Node screen = route.createScreen();
        getChildren().setAll(screen);
    }

    public Route getCurrentRoute() {
        return currentRoute;
    }

    public void clear() {
        getChildren().clear();
        currentRoute = null;
    }
}
