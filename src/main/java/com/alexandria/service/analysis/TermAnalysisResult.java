package com.alexandria.service.analysis;

import java.util.List;

public record TermAnalysisResult(
        String term,
        int totalOccurrences,
        double relativeFrequency,
        int sentenceCount,
        int paragraphCount,
        List<WordFrequency> neighboringWords) {}
