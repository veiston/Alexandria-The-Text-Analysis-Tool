package com.alexandria.service;

import com.alexandria.service.analysis.*;

import java.util.*;
import java.util.regex.*;

import static com.alexandria.service.AnalysisUtils.*;

public class TermAnalysisService implements TermAnalysisServiceINT {

    @Override
    public TermAnalysisResult analyzeTerm(String content, String term) {
        var words = extractWords(content);
        var target = term.strip().toLowerCase(Locale.ROOT);
        var termPattern = Pattern.compile("\\b" + Pattern.quote(target) + "\\b", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

        var occurrences = countMatches(content, termPattern);
        var relativeFreq = calculateRelativeFrequency(occurrences, words.size());
        var neighbors = findNeighboringWords(words, extractWords(target), 5);

        return new TermAnalysisResult(
            term, occurrences, relativeFreq,
            countContaining(content, SENTENCE_PATTERN, termPattern),
            countContainingParagraphs(content, termPattern),
            getTopWords(neighbors, words.size(), 5)
        );
    }

    private Map<String, Long> findNeighboringWords(List<String> words, List<String> targetWords, int radius) {
        var neighbors = new HashMap<String, Long>();
        int tLen = targetWords.size();
        if (tLen == 0 || words.size() < tLen) return neighbors;

        for (int i = 0; i <= words.size() - tLen; i++) {
            boolean match = true;
            for (int k = 0; k < tLen; k++) {
                if (!words.get(i + k).equals(targetWords.get(k))) {
                    match = false;
                    break;
                }
            }
            if (match) {
                int start = Math.max(0, i - radius);
                int end = Math.min(words.size() - 1, i + tLen - 1 + radius);
                for (int j = start; j <= end; j++) {
                    if ((j < i || j >= i + tLen) && !STOP_WORDS.contains(words.get(j))) {
                        neighbors.merge(words.get(j), 1L, Long::sum);
                    }
                }
            }
        }
        return neighbors;
    }
}
