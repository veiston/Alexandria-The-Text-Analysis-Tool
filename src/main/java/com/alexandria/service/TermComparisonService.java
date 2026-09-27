package com.alexandria.service;

import com.alexandria.service.analysis.TermComparisonResult;
import com.alexandria.service.analysis.TermComparisonResult.TermTextOccurrence;

import java.util.*;
import java.util.regex.*;

import static com.alexandria.service.AnalysisUtils.*;



public class TermComparisonService {

    public TermComparisonResult compareTerm(Map<Integer, String> textsById, Map<Integer, String> titlesById, String term) {
        return compareTerm(textsById, titlesById, term, false);
    }

    public TermComparisonResult compareTerm(Map<Integer, String> textsById, Map<Integer, String> titlesById, String term, boolean ignoreStopWords) {
        if (term == null || term.isBlank()) {
            return null;
        }

        if (textsById == null || textsById.isEmpty()) {
            return new TermComparisonResult(term, Collections.emptyList());
        }

        if (ignoreStopWords && STOP_WORDS.contains(term.strip().toLowerCase(Locale.ROOT))) {
            return new TermComparisonResult(term, Collections.emptyList());
        }

        var pattern = Pattern.compile("\\b" + Pattern.quote(term.strip()) + "\\b", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

        var occurrences = textsById.entrySet().stream().map(entry -> {
            int textId = entry.getKey();
            String content = entry.getValue();
            int count = countMatches(content, pattern);
            int total = countMatches(content, WORD_PATTERN);
            String title = "Text " + textId;
            if (titlesById != null && titlesById.containsKey(textId)) {
                title = titlesById.get(textId);
            }

            return new TermTextOccurrence(textId, title, count, calculateRelativeFrequency(count, total));
        }).toList();

        return new TermComparisonResult(term, occurrences);
    }
}
