package com.alexandria.service;

import com.alexandria.service.analysis.*;
import com.alexandria.service.analysis.TextComparisonResult.TextComparisonRow;

import java.util.*;
import java.util.stream.*;

import static com.alexandria.service.AnalysisUtils.*;



public class TextComparisonService {

    public TextComparisonResult compareTexts(Map<Integer, String> textsById, int limit) {
        if (textsById == null || textsById.isEmpty()) {
            return new TextComparisonResult(Collections.emptyList(), Collections.emptyList());
        }

        var extractedWords = textsById.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, e -> extractWords(e.getValue())));

        var wordFrequencies = extractedWords.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, e -> countWordFrequencies(e.getValue())));

        var candidateWords = wordFrequencies.values().stream()
            .flatMap(freqs -> freqs.keySet().stream())
            .collect(Collectors.toSet());

        long maxRows;
        if (limit > 0) {
            maxRows = limit;
        } else {
            maxRows = Long.MAX_VALUE;
        }

        var rows = candidateWords.stream()
            .map(word -> createComparisonRow(word, extractedWords, wordFrequencies))
            .sorted(Comparator.comparingInt(this::sumComparisonCounts).reversed().thenComparing(TextComparisonRow::word))
            .limit(maxRows)
            .toList();

        return new TextComparisonResult(new ArrayList<>(textsById.keySet()), rows);
    }

    private TextComparisonRow createComparisonRow(String word, Map<Integer, List<String>> extractedWords, Map<Integer, Map<String, Long>> wordFrequencies) {
        var counts = new HashMap<Integer, Integer>();
        var relFreqs = new HashMap<Integer, Double>();

        extractedWords.forEach((id, words) -> {
            int count = wordFrequencies.get(id).getOrDefault(word, 0L).intValue();
            counts.put(id, count);
            relFreqs.put(id, calculateRelativeFrequency(count, words.size()));
        });

        return new TextComparisonRow(word, counts, relFreqs);
    }

    private int sumComparisonCounts(TextComparisonRow row) {
        return row.countsByTextId().values().stream().mapToInt(Integer::intValue).sum();
    }
}
