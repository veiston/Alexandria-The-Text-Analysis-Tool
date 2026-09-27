package com.alexandria.utils;

import java.util.prefs.Preferences;

public final class UserGuideSettings {
    private static final String SHOW_ON_STARTUP_KEY = "showUserGuideOnStartup";
    private static final Preferences preferences = Preferences.userNodeForPackage(UserGuideSettings.class);

    private UserGuideSettings() {
    }

    public static boolean isShownOnStartup() {
        return preferences.getBoolean(SHOW_ON_STARTUP_KEY, true);
    }

    public static void setShownOnStartup(boolean enabled) {
        preferences.putBoolean(SHOW_ON_STARTUP_KEY, enabled);
    }
}
