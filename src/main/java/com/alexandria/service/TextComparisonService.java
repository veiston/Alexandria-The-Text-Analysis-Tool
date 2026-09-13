package com.alexandria.service;

import com.alexandria.service.analysis.*;
import com.alexandria.service.analysis.TextComparisonResult.TextComparisonRow;

import java.util.*;
import java.util.stream.*;

import static com.alexandria.service.AnalysisUtils.*;



public class TextComparisonService {

    private final TextAnalysisService textAnalysisService = new TextAnalysisService();

    public TextComparisonResult compareTexts(Map<Integer, String> textsById, int limit) {
        if (textsById == null || textsById.isEmpty()) return new TextComparisonResult(Collections.emptyList(), Collections.emptyList());

        var analyses = textsById.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, e -> textAnalysisService.analyzeText(e.getValue())));

        var sharedWords = analyses.values().stream()
            .flatMap(res -> res.frequentWords().stream().map(WordFrequency::word))
            .collect(Collectors.toSet());

        long maxRows;
        if (limit > 0) {
            maxRows = limit;
        } else {
            maxRows = Long.MAX_VALUE;
        }

        var rows = sharedWords.stream()
            .map(word -> createComparisonRow(word, analyses))
            .sorted(Comparator.comparingInt(this::sumComparisonCounts).reversed())
            .limit(maxRows)
            .toList();

        return new TextComparisonResult(new ArrayList<>(textsById.keySet()), rows);
    }

    private TextComparisonRow createComparisonRow(String word, Map<Integer, TextAnalysisResult> analyses) {
        var counts = new HashMap<Integer, Integer>();
        var relFreqs = new HashMap<Integer, Double>();

        analyses.forEach((id, res) -> {
            int count = res.frequentWords().stream()
                .filter(w -> w.word().equals(word))
                .mapToInt(WordFrequency::count)
                .findFirst().orElse(0);

            counts.put(id, count);
            relFreqs.put(id, calculateRelativeFrequency(count, res.totalWords()));
        });

        return new TextComparisonRow(word, counts, relFreqs);
    }

    private int sumComparisonCounts(TextComparisonRow row) {
        return row.countsByTextId().values().stream().mapToInt(Integer::intValue).sum();
    }
}
