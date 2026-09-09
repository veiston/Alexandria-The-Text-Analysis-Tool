package com.alexandria.service.analysis;

import java.util.List;

public record TextAnalysisResult(
        int totalWords,
        int uniqueWords,
        int totalSentences,
        int totalParagraphs,
        List<WordFrequency> frequentWords,
        List<TextFragment> importantFragments) {}
