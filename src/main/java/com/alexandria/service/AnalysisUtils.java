package com.alexandria.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.alexandria.service.analysis.WordFrequency;

class AnalysisUtils {

    static Integer resolveIndex(int offset, List<Integer> offsets) {
        if (offsets == null || offsets.isEmpty()) {
            return null;
        }
        int idx = Collections.binarySearch(offsets, offset);
        if (idx >= 0) {
            return idx + 1;
        }
        int insertionPoint = -idx - 1;
        return Math.max(1, insertionPoint);
    }

    static Integer resolvePage(int offset, List<Integer> pageOffsets) {
        return resolveIndex(offset, pageOffsets);
    }

    static Integer resolveParagraph(int offset, List<Integer> paragraphOffsets) {
        return resolveIndex(offset, paragraphOffsets);
    }

    static List<Integer> resolveParagraphOffsets(String content) {
        if (content == null || content.isBlank()) {
            return Collections.emptyList();
        }
        List<Integer> offsets = new ArrayList<>();
        String[] paragraphs = PARAGRAPH_SPLIT_PATTERN.split(content);
        int searchFrom = 0;
        for (String paragraph : paragraphs) {
            if (paragraph.isBlank()) {
                continue;
            }
            int idx = content.indexOf(paragraph, searchFrom);
            if (idx >= 0) {
                offsets.add(idx);
                searchFrom = idx + paragraph.length();
            }
        }
        if (offsets.isEmpty()) {
            offsets.add(0);
        }
        return offsets;
    }


    static final Pattern WORD_PATTERN = Pattern.compile("[\\p{L}]+");
    static final Pattern SENTENCE_PATTERN = Pattern.compile("[^.!?]+[.!?]*");
    static final Pattern PARAGRAPH_SPLIT_PATTERN = Pattern.compile("(\\r?\\n\\s*){2,}");

    static final Set<String> STOP_WORDS = Set.of(
        "a", "about", "above", "after", "again", "against", "all", "am", "an", "and",
        "any", "are", "as", "at", "be", "because", "been", "before", "being", "below",
        "between", "both", "but", "by", "can", "cannot", "could", "did", "do", "does",
        "doing", "down", "during", "each", "few", "for", "from", "further", "had", "has",
        "have", "having", "he", "her", "here", "hers", "herself", "him", "himself", "his",
        "how", "i", "if", "in", "into", "is", "it", "its", "itself", "me",
        "more", "most", "my", "myself", "no", "nor", "not", "of", "off", "on",
        "once", "only", "or", "other", "our", "ours", "ourselves", "out", "over", "own",
        "s", "same", "she", "should", "so", "some", "such", "than", "that", "the", "their",
        "theirs", "them", "themselves", "then", "there", "these", "they", "thou", "thy", "this", "those", "through",
        "to", "too", "under", "until", "up", "very", "was", "we", "were", "what",
        "when", "where", "which", "while", "who", "whom", "why", "will", "with", "would",
        "you", "your", "yours", "yourself", "yourselves"
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
