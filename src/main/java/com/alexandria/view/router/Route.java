package com.alexandria.view.router;

import com.alexandria.view.screens.*;
import javafx.scene.Node;

import java.util.function.Supplier;

public enum Route {
    LIBRARY("library", LibraryScreen::new),
    ANALYZE("analyze", AnalyseScreen::new),
    COMPARE("compare", CompareScreen::new),
    ARCHIVE("archive", ArchiveScreen::new),
    SETTINGS("settings", SettingsScreen::new),
    USERGUIDE("userguide", UserGuideScreen::new),
    PROFILE("profile", ProfileScreen::new);

    private final String id;
    private final Supplier<Node> screenFactory;

    Route(String id, Supplier<Node> screenFactory) {
        this.id = id;
        this.screenFactory = screenFactory;
    }

    public String id() {
        return id;
    }

    public Node createScreen() {
        return screenFactory.get();
    }

    public static Route fromId(String id) {
        for (Route route : values()) {
            if (route.id.equals(id))
                return route;
        }
        throw new IllegalArgumentException("Unknown route id: " + id);
    }
}