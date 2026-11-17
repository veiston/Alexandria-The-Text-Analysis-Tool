package com.alexandria.utils;

import java.util.prefs.Preferences;

import com.alexandria.model.Language;

public final class LanguageSettings {
    private static final String LANGUAGE_KEY = "language";
    private static final Preferences PREFERENCES = Preferences.userNodeForPackage(LanguageSettings.class);

    private LanguageSettings() {
    }

    public static Language getLanguage() {
        String languageName = PREFERENCES.get(LANGUAGE_KEY, Language.ENGLISH.name());
        try {
            return Language.valueOf(languageName);
        } catch (IllegalArgumentException exception) {
            return Language.ENGLISH;
        }
    }

    public static void setLanguage(Language language) {
        PREFERENCES.put(LANGUAGE_KEY, language.name());
    }
}
