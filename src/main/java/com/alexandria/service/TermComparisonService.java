package com.alexandria.service;

import com.alexandria.service.analysis.TermComparisonResult;
import com.alexandria.service.analysis.TermComparisonResult.TermTextOccurrence;

import java.util.*;
import java.util.regex.*;

import static com.alexandria.service.AnalysisUtils.*;



public class TermComparisonService {

    public TermComparisonResult compareTerm(Map<Integer, String> textsById, Map<Integer, String> titlesById, String term) {
        if (textsById == null || textsById.isEmpty() || term == null || term.isBlank()) return new TermComparisonResult(term, Collections.emptyList());

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
