package com.alexandria.model;

import java.util.Locale;

import com.alexandria.service.LocalizationService;

public enum Language {
    ENGLISH("English", LocalizationService.ENGLISH),
    JAPANESE("Japanese (日本語)", LocalizationService.JAPANESE),
    RUSSIAN("Russian (Русский)", LocalizationService.RUSSIAN);

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
