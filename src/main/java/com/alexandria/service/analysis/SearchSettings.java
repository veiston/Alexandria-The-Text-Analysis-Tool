package com.alexandria.service.analysis;

/** Independent search toggles — not mutually exclusive, unlike a single "mode". */
public record SearchSettings(boolean caseSensitive, boolean fuzzy, boolean wholeWordsOnly) {

    public static SearchSettings defaults() {
        return new SearchSettings(false, false, false);
    }
}