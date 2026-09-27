package com.alexandria.service.analysis;

/** 
 * Independent search toggles — not mutually exclusive, unlike a single "mode". 
 * caseSensitive = Exact match, fuzzy = Syninoums, wholeWordsOnly = RegEx
*/
public record SearchSettings(boolean caseSensitive, boolean fuzzy, boolean wholeWordsOnly, boolean ignoreStopWords) {

    public SearchSettings(boolean caseSensitive, boolean fuzzy, boolean wholeWordsOnly) {
        this(caseSensitive, fuzzy, wholeWordsOnly, false);
    }

    public static SearchSettings defaults() {
        return new SearchSettings(false, false, false, false);
    }
}