package com.alexandria.service;

import com.alexandria.service.analysis.*;

import java.util.*;

import static com.alexandria.service.AnalysisUtils.*;

public class TextAnalysisService implements TextAnalysisServiceINT {

    @Override
    public TextAnalysisResult analyzeText(String content) {
        return analyzeText(content, Collections.emptyList());
    }

    public TextAnalysisResult analyzeText(String content, List<Integer> pageOffsets) {
        var words = extractWords(content);
        var frequencies = countWordFrequencies(words);

        return new TextAnalysisResult(
            words.size(),
            frequencies.size(),
            countMatches(content, SENTENCE_PATTERN),
            countParagraphs(content),
            getTopWords(frequencies, words.size(), 5),
            getImportantFragments(content, frequencies, 5, pageOffsets)
        );
    }

    private List<TextFragment> getImportantFragments(String content, Map<String, Long> globalFreqs, int limit, List<Integer> pageOffsets) {
        if (content == null || content.isBlank()) return Collections.emptyList();

        var fragments = new ArrayList<TextFragment>();
        var paragraphs = PARAGRAPH_SPLIT_PATTERN.split(content.strip());

        int charOffset = 0;
        for (int i = 0; i < paragraphs.length; i++) {
            int pIndex = i + 1;
            String paragraph = paragraphs[i];
            
            int pStart = content.indexOf(paragraph, charOffset);
            if (pStart >= 0) charOffset = pStart;
            
            Integer page = resolvePage(charOffset, pageOffsets);

            SENTENCE_PATTERN.matcher(paragraph).results()
                .map(m -> m.group().replaceAll("\\s+", " ").strip())
                .filter(s -> !s.isBlank())
                .forEach(sentence -> {
                    int score = extractWords(sentence).stream().mapToInt(w -> globalFreqs.getOrDefault(w, 0L).intValue()).sum();
                    fragments.add(new TextFragment(sentence, score, page, pIndex));
                });
                
            charOffset += paragraph.length();
        }

        return fragments.stream()
            .sorted(Comparator.comparingInt(TextFragment::score).reversed())
            .limit(limit)
            .toList();
    }
}
