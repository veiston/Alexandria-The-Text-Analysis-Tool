package com.alexandria.utils;

import java.util.prefs.Preferences;

public final class UserGuideSettings {
    private static final String SHOW_ON_STARTUP_KEY = "showUserGuideOnStartup";
    private static final Preferences PREFERENCES = Preferences.userNodeForPackage(UserGuideSettings.class);

    private UserGuideSettings() {
    }

    public static boolean isShownOnStartup() {
        return PREFERENCES.getBoolean(SHOW_ON_STARTUP_KEY, true);
    }

    public static void setShownOnStartup(boolean enabled) {
        PREFERENCES.putBoolean(SHOW_ON_STARTUP_KEY, enabled);
    }
}
