package com.alexandria.view.components.shared.document.highlight;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class TxtHighlight {

    public static final String SEARCH_STYLE_CLASS = "txt-search-highlight";
    public static final String QUOTATION_STYLE_CLASS = "txt-quotation-highlight";
    public record Range(int start, int end) {
    }
    private TxtHighlight() {
    }

    public static List<Range> findRanges(String pageText, String term) {
        List<Range> ranges = new ArrayList<>();

        if (pageText == null || term == null || term.isBlank()) {
            return ranges;
        }

        String haystack = pageText.toLowerCase(Locale.ROOT);
        String needle = term.strip().toLowerCase(Locale.ROOT);

        int index = haystack.indexOf(needle);

        while (index >= 0) {
            ranges.add(new Range(index, index + needle.length()));
            index = haystack.indexOf(needle, index + 1);
        }

        return ranges;
    }
}