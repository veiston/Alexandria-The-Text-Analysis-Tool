package com.alexandria.service;

import java.util.*;
import java.util.regex.*;
import java.util.stream.*;

import com.alexandria.service.analysis.WordFrequency;

class AnalysisUtils {

    static Integer resolvePage(int offset, List<Integer> pageOffsets) {
        if (pageOffsets == null || pageOffsets.isEmpty()) return null;
        int idx = Collections.binarySearch(pageOffsets, offset);
        if (idx >= 0) return idx + 1;
        int insertionPoint = -idx - 1;
        return Math.max(1, insertionPoint);
    }

    static final Pattern WORD_PATTERN = Pattern.compile("[\\p{L}]+");
    static final Pattern SENTENCE_PATTERN = Pattern.compile("[^.!?]+[.!?]*");
    static final Pattern PARAGRAPH_SPLIT_PATTERN = Pattern.compile("(\\r?\\n\\s*){2,}");

    static final Set<String> STOP_WORDS = Set.of(
        "the", "be", "to", "of", "and", "a", "in", "that", "have", "i", "it", "for", "not", "on", "with", "he", "as", "you", "do", "at"
    );

    static List<String> extractWords(String content) {
        if (content == null) return Collections.emptyList();
        return WORD_PATTERN.matcher(content).results().map(m -> m.group().toLowerCase(Locale.ROOT)).toList();
    }

    static Map<String, Long> countWordFrequencies(List<String> words) {
        return words.stream()
            .filter(w -> !STOP_WORDS.contains(w))
            .collect(Collectors.groupingBy(w -> w, Collectors.counting()));
    }

    static int countMatches(String content, Pattern pattern) {
        if (content == null) return 0;
        return (int) pattern.matcher(content).results().count();
    }

    static int countParagraphs(String content) {
        if (content == null || content.isBlank()) return 0;
        return (int) Arrays.stream(PARAGRAPH_SPLIT_PATTERN.split(content.strip())).filter(p -> !p.isBlank()).count();
    }

    static int countContaining(String content, Pattern splitter, Pattern searcher) {
        if (content == null) return 0;
        return (int) splitter.matcher(content).results().filter(m -> searcher.matcher(m.group()).find()).count();
    }

    static int countContainingParagraphs(String content, Pattern searcher) {
        if (content == null || content.isBlank()) return 0;
        return (int) Arrays.stream(PARAGRAPH_SPLIT_PATTERN.split(content.strip())).filter(p -> searcher.matcher(p).find()).count();
    }

    static double calculateRelativeFrequency(int count, double total) {
        if (total > 0) {
            return (count / total) * 1000.0;
        }
        return 0.0;
    }

    static List<WordFrequency> getTopWords(Map<String, Long> frequencies, double totalWords, int limit) {
        return frequencies.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(limit)
            .map(e -> new WordFrequency(e.getKey(), e.getValue().intValue(), calculateRelativeFrequency(e.getValue().intValue(), totalWords), null, null))
            .toList();
    }
}
