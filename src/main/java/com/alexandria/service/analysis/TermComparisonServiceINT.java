package com.alexandria.service.analysis;

import java.util.Map;

public interface TermComparisonServiceINT {
    default TermComparisonResult compareTerm(
            Map<Integer, String> textsById,
            Map<Integer, String> titlesById,
            String term) {
        return compareTerm(textsById, titlesById, term, false);
    }

    TermComparisonResult compareTerm(
            Map<Integer, String> textsById,
            Map<Integer, String> titlesById,
            String term,
            boolean ignoreStopWords);
}
