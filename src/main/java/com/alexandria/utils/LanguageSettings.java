package com.alexandria.utils;

import java.util.prefs.Preferences;

import com.alexandria.model.Language;

public final class LanguageSettings {
    private static final String LANGUAGE_KEY = "language";
    private static final Preferences preferences = Preferences.userNodeForPackage(LanguageSettings.class);

    private LanguageSettings() {
    }

    public static Language getLanguage() {
        String languageName = preferences.get(LANGUAGE_KEY, Language.ENGLISH.name());
        try {
            return Language.valueOf(languageName);
        } catch (IllegalArgumentException exception) {
            return Language.ENGLISH;
        }
    }

    public static void setLanguage(Language language) {
        preferences.put(LANGUAGE_KEY, language.name());
    }
}
