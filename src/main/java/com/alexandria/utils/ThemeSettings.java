package com.alexandria.utils;

import java.util.prefs.Preferences;

import javafx.scene.Node;

public final class ThemeSettings {
    private static final String DARK_THEME_KEY = "darkTheme";
    private static final String DARK_THEME_CLASS = "dark";
    private static final Preferences PREFERENCES = Preferences.userNodeForPackage(ThemeSettings.class);

    private ThemeSettings() {
    }

    public static boolean isDarkTheme() {
        return PREFERENCES.getBoolean(DARK_THEME_KEY, false);
    }

    public static void setDarkTheme(Node root, boolean enabled) {
        PREFERENCES.putBoolean(DARK_THEME_KEY, enabled);
        apply(root);
    }

    public static void apply(Node root) {
        if (root == null) {
            return;
        }

        if (isDarkTheme()) {
            root.getStyleClass().add(DARK_THEME_CLASS);
        } else {
            root.getStyleClass().remove(DARK_THEME_CLASS);
        }
    }
}
