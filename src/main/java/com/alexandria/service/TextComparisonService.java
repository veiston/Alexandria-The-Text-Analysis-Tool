package com.alexandria.service;

import com.alexandria.service.analysis.*;
import com.alexandria.service.analysis.TextComparisonResult.TextComparisonRow;

import java.util.*;
import java.util.stream.*;

import static com.alexandria.service.AnalysisUtils.*;



public class TextComparisonService implements TextComparisonServiceINT {

    private static final int MIN_PARAGRAPH_LENGTH = 50;
    private static final int MIN_SHARED_COMMON_WORDS = 3;
    private static final int MAX_PARAGRAPH_THRESHOLD = 5000;
    private static final int MAX_PARAGRAPH_MATCHES = 5;

    public TextComparisonResult compareTexts(Map<Integer, String> textsById, Map<Integer, List<Integer>> pageOffsetsById, int limit) {
        if (textsById == null || textsById.isEmpty()) {
            return new TextComparisonResult(Collections.emptyList(), Collections.emptyList(), 0.0, "Low", Collections.emptyList());
        }

        var extractedWords = textsById.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, e -> extractWords(e.getValue())));

        var wordFrequencies = extractedWords.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, e -> countWordFrequencies(e.getValue())));

        var intersection = wordFrequencies.values().stream()
            .map(Map::keySet)
            .reduce((s1, s2) -> {
                Set<String> temp = new HashSet<>(s1);
                temp.retainAll(s2);
                return temp;
            })
            .orElse(Collections.emptySet());

        double similarityScore = 0.0;
        if (textsById.size() >= 2 && !wordFrequencies.isEmpty()) {
            var iterator = wordFrequencies.values().iterator();
            similarityScore = calculateCosineSimilarity(iterator.next(), iterator.next());
        }

        String similarityBand = TextComparisonResult.SimilarityBand.fromScore(similarityScore).label();

        long maxRows = Long.MAX_VALUE;
        if (limit > 0) {
            maxRows = limit;
        }

        var rows = intersection.stream()
            .map(word -> createComparisonRow(word, extractedWords, wordFrequencies))
            .sorted(Comparator.comparingInt(this::sumComparisonCounts).reversed().thenComparing(TextComparisonRow::word))
            .limit(maxRows)
            .toList();

        List<TextComparisonResult.ParagraphMatch> similarParagraphs = findSimilarParagraphs(textsById, pageOffsetsById, intersection);

        return new TextComparisonResult(new ArrayList<>(textsById.keySet()), rows, similarityScore, similarityBand, similarParagraphs);
    }

    private double calculateCosineSimilarity(Map<String, Long> vec1, Map<String, Long> vec2) {
        double dotProduct = 0.0;
        double norm1 = 0.0;
        for (Map.Entry<String, Long> entry : vec1.entrySet()) {
            long v1 = entry.getValue();
            norm1 += v1 * v1;
            Long v2 = vec2.get(entry.getKey());
            if (v2 != null) {
                dotProduct += v1 * v2;
            }
        }

        double norm2 = 0.0;
        for (long v2 : vec2.values()) {
            norm2 += v2 * v2;
        }

        if (norm1 == 0.0 || norm2 == 0.0) {
            return 0.0;
        }
        return (dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2))) * 100.0;
    }

    private record ParagraphMatchCandidate(int firstIndex, int secondIndex, double score, int start1, String p1, int start2, String p2) {}

    private List<TextComparisonResult.ParagraphMatch> findSimilarParagraphs(Map<Integer, String> textsById, Map<Integer, List<Integer>> pageOffsetsById, Set<String> commonWords) {
        if (textsById == null || textsById.size() < 2 || commonWords.isEmpty()) {
            return Collections.emptyList();
        }

        var textEntries = new ArrayList<>(textsById.entrySet());
        var entry1 = textEntries.get(0);
        var entry2 = textEntries.get(1);

        String text1 = entry1.getValue();
        String text2 = entry2.getValue();

        if (text1 == null || text1.isBlank() || text2 == null || text2.isBlank()) {
            return Collections.emptyList();
        }

        if (pageOffsetsById == null) {
            pageOffsetsById = Collections.emptyMap();
        }
        List<Integer> pages1 = pageOffsetsById.getOrDefault(entry1.getKey(), Collections.emptyList());
        List<Integer> pages2 = pageOffsetsById.getOrDefault(entry2.getKey(), Collections.emptyList());

        String[] paragraphs1 = PARAGRAPH_SPLIT_PATTERN.split(text1);
        String[] paragraphs2 = PARAGRAPH_SPLIT_PATTERN.split(text2);

        if (paragraphs1.length > MAX_PARAGRAPH_THRESHOLD || paragraphs2.length > MAX_PARAGRAPH_THRESHOLD) {
            return Collections.emptyList();
        }

        List<ParagraphMatchCandidate> candidates = new ArrayList<>();

        int charOffset1 = 0;
        for (int i = 0; i < paragraphs1.length; i++) {
            String p1 = paragraphs1[i];
            int start1 = text1.indexOf(p1, charOffset1);
            if (start1 < 0) {
                continue;
            }
            charOffset1 = start1;

            if (p1.length() < MIN_PARAGRAPH_LENGTH) {
                charOffset1 += p1.length();
                continue;
            }

            Set<String> words1 = new HashSet<>(extractWords(p1));
            words1.retainAll(commonWords);
            if (words1.isEmpty()) {
                charOffset1 += p1.length();
                continue;
            }

            int charOffset2 = 0;
            for (int j = 0; j < paragraphs2.length; j++) {
                String p2 = paragraphs2[j];
                int start2 = text2.indexOf(p2, charOffset2);
                if (start2 < 0) {
                    continue;
                }
                charOffset2 = start2;

                if (p2.length() < MIN_PARAGRAPH_LENGTH) {
                    charOffset2 += p2.length();
                    continue;
                }

                Set<String> words2 = new HashSet<>(extractWords(p2));
                words2.retainAll(commonWords);
                if (words2.isEmpty()) {
                    charOffset2 += p2.length();
                    continue;
                }

                Set<String> shared = new HashSet<>(words1);
                shared.retainAll(words2);
                if (shared.size() >= MIN_SHARED_COMMON_WORDS) {
                    double score = ((double) shared.size() / Math.max(words1.size(), words2.size())) * 100.0;
                    candidates.add(new ParagraphMatchCandidate(i, j, score, start1, p1, start2, p2));
                }
                charOffset2 += p2.length();
            }
            charOffset1 += p1.length();
        }

        candidates.sort(Comparator.comparingDouble(ParagraphMatchCandidate::score).reversed());

        List<TextComparisonResult.ParagraphMatch> results = new ArrayList<>();
        Set<Integer> used1 = new HashSet<>();
        Set<Integer> used2 = new HashSet<>();

        // Greedily match paragraphs one-to-one. ⚡
        for (ParagraphMatchCandidate c : candidates) {
            if (results.size() >= MAX_PARAGRAPH_MATCHES) {
                break;
            }
            if (!used1.contains(c.firstIndex) && !used2.contains(c.secondIndex)) {
                used1.add(c.firstIndex);
                used2.add(c.secondIndex);

                var snip1 = new TextComparisonResult.ParagraphSnippet(c.p1.strip(), c.start1, c.start1 + c.p1.length(), resolvePage(c.start1, pages1), c.firstIndex + 1);
                var snip2 = new TextComparisonResult.ParagraphSnippet(c.p2.strip(), c.start2, c.start2 + c.p2.length(), resolvePage(c.start2, pages2), c.secondIndex + 1);
                results.add(new TextComparisonResult.ParagraphMatch(snip1, snip2, c.score));
            }
        }

        return results;
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
