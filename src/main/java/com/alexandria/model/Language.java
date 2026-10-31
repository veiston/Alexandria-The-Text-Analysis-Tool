package com.alexandria.model;

public enum Language {
    ENGLISH("English"),
    JAPANESE("Japanese (日本語)"),
    RUSSIAN("Russian (Русский)");

    private final String displayName;

    Language(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
