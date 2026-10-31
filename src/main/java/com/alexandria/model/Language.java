package com.alexandria.model;

import java.util.Locale;

public enum Language {
    ENGLISH("English", Locale.US),
    JAPANESE("Japanese (日本語)", Locale.JAPAN),
    RUSSIAN("Russian (Русский)", new Locale("ru", "RU"));

    private final String displayName;
    private final Locale locale;

    Language(String displayName, Locale locale) {
        this.displayName = displayName;
        this.locale = locale;
    }

    public Locale getLocale() {
        return locale;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
