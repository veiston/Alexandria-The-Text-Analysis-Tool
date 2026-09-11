package com.alexandria.service;

import com.alexandria.model.TermComparisonResult;
import com.alexandria.model.TermComparisonResult.TermTextOccurrence;
import com.alexandria.model.TextComparisonResult;
import com.alexandria.model.TextComparisonResult.TextComparisonRow;
import com.alexandria.service.analysis.*;

import java.util.*;
import java.util.regex.*;
import java.util.stream.*;

public class TextAnalysisService implements TextAnalysisServiceINT, TermAnalysisServiceINT, SearchServiceINT {

    private static final Pattern WORD_PATTERN = Pattern.compile("[\\p{L}]+");
    private static final Pattern SENTENCE_PATTERN = Pattern.compile("[^.!?]+[.!?]*");
    private static final Pattern PARAGRAPH_SPLIT_PATTERN = Pattern.compile("(\\r?\\n\\s*){2,}");

    private static final Set<String> STOP_WORDS = Set.of(
        "the", "be", "to", "of", "and", "a", "in", "that", "have", "i", "it", "for", "not", "on", "with", "he", "as", "you", "do", "at"
    );

    @Override
    public TextAnalysisResult analyzeText(String content) {
        var words = extractWords(content);
        var frequencies = countWordFrequencies(words);
        
        return new TextAnalysisResult(
            words.size(),
            frequencies.size(),
            countMatches(content, SENTENCE_PATTERN),
            countParagraphs(content),
            getTopWords(frequencies, words.size(), 5), 
            getImportantFragments(content, frequencies, 5)
        );
    }

    @Override
    public TermAnalysisResult analyzeTerm(String content, String term) {
        var words = extractWords(content);
        var target = term.strip().toLowerCase(Locale.ROOT);
        var termPattern = Pattern.compile("\\b" + Pattern.quote(target) + "\\b", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        
        var occurrences = (int) words.stream().filter(target::equals).count();
        var relativeFreq = calculateRelativeFrequency(occurrences, words.size());
        var neighbors = findNeighboringWords(words, target, 5);

        return new TermAnalysisResult(
            term, occurrences, relativeFreq,
            countContaining(content, SENTENCE_PATTERN, termPattern),
            countContainingParagraphs(content, termPattern),
            getTopWords(neighbors, sumMapValues(neighbors), 5)
        );
    }

    @Override
    public List<SearchMatch> search(String content, String term, SearchSettings setting) {
        if (content == null || content.isBlank() || term == null || term.isBlank()) return Collections.emptyList();

        if (setting.fuzzy()) {
            return new FuzzySearchService().findWithFuzzy(content, term);
        }

        var query = setting.wholeWordsOnly() ? "\\b" + Pattern.quote(term) + "\\b" : Pattern.quote(term);
        var flags = setting.caseSensitive() ? Pattern.UNICODE_CASE : Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE;
        
        return Pattern.compile(query, flags).matcher(content).results()
                .map(m -> new SearchMatch(term, m.start(), m.end(), null, null))
                .toList();
    }

    public TextComparisonResult compareTexts(Map<Integer, String> textsById, int limit) {
        if (textsById == null || textsById.isEmpty()) return new TextComparisonResult(Collections.emptyList(), Collections.emptyList());

        var analyses = textsById.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, e -> analyzeText(e.getValue())));
            
        var sharedWords = analyses.values().stream()
            .flatMap(res -> res.frequentWords().stream().map(WordFrequency::word))
            .collect(Collectors.toSet());

        var rows = sharedWords.stream()
            .map(word -> createComparisonRow(word, analyses))
            .sorted(Comparator.comparingInt(this::sumComparisonCounts).reversed())
            .limit(limit > 0 ? limit : Long.MAX_VALUE)
            .toList();

        return new TextComparisonResult(new ArrayList<>(textsById.keySet()), rows);
    }

    public TermComparisonResult compareTerm(Map<Integer, String> textsById, Map<Integer, String> titlesById, String term) {
        if (textsById == null || textsById.isEmpty() || term == null || term.isBlank()) return new TermComparisonResult(term, Collections.emptyList());

        var pattern = Pattern.compile("\\b" + Pattern.quote(term.strip()) + "\\b", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

        var occurrences = textsById.entrySet().stream().map(entry -> {
            int textId = entry.getKey();
            String content = entry.getValue();
            int count = countMatches(content, pattern);
            int total = countMatches(content, WORD_PATTERN);
            String title = titlesById != null && titlesById.containsKey(textId) ? titlesById.get(textId) : "Text " + textId;
            
            return new TermTextOccurrence(textId, title, count, calculateRelativeFrequency(count, total));
        }).toList();

        return new TermComparisonResult(term, occurrences);
    }

    // --- Private Helper Methods ---

    private Map<String, Long> countWordFrequencies(List<String> words) {
        return words.stream()
            .filter(w -> !STOP_WORDS.contains(w))
            .collect(Collectors.groupingBy(w -> w, Collectors.counting()));
    }

    private Map<String, Long> findNeighboringWords(List<String> words, String target, int radius) {
        var neighbors = new HashMap<String, Long>();
        for (int i = 0; i < words.size(); i++) {
            if (words.get(i).equals(target)) {
                int start = Math.max(0, i - radius);
                int end = Math.min(words.size() - 1, i + radius);
                for (int j = start; j <= end; j++) {
                    if (j != i && !STOP_WORDS.contains(words.get(j))) {
                        neighbors.merge(words.get(j), 1L, Long::sum);
                    }
                }
            }
        }
        return neighbors;
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

    private List<WordFrequency> getTopWords(Map<String, Long> frequencies, double totalWords, int limit) {
        return frequencies.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(limit)
            .map(e -> new WordFrequency(e.getKey(), e.getValue().intValue(), calculateRelativeFrequency(e.getValue().intValue(), totalWords), null, null))
            .toList();
    }

    private List<TextFragment> getImportantFragments(String content, Map<String, Long> globalFreqs, int limit) {
        if (content == null || content.isBlank()) return Collections.emptyList();
        
        var fragments = new ArrayList<TextFragment>();
        var paragraphs = PARAGRAPH_SPLIT_PATTERN.split(content.strip());
        
        for (int i = 0; i < paragraphs.length; i++) {
            int pIndex = i + 1;
            SENTENCE_PATTERN.matcher(paragraphs[i]).results()
                .map(m -> m.group().replaceAll("\\s+", " ").strip())
                .filter(s -> !s.isBlank())
                .forEach(sentence -> {
                    int score = extractWords(sentence).stream().mapToInt(w -> globalFreqs.getOrDefault(w, 0L).intValue()).sum();
                    fragments.add(new TextFragment(sentence, score, null, pIndex));
                });
        }
        
        return fragments.stream()
            .sorted(Comparator.comparingInt(TextFragment::score).reversed())
            .limit(limit)
            .toList();
    }

    private List<String> extractWords(String content) {
        return WORD_PATTERN.matcher(content == null ? "" : content).results().map(m -> m.group().toLowerCase(Locale.ROOT)).toList();
    }

    private int countMatches(String content, Pattern pattern) {
        return (int) pattern.matcher(content == null ? "" : content).results().count();
    }

    private int countParagraphs(String content) {
        if (content == null || content.isBlank()) return 0;
        return (int) Arrays.stream(PARAGRAPH_SPLIT_PATTERN.split(content.strip())).filter(p -> !p.isBlank()).count();
    }

    private int countContaining(String content, Pattern splitter, Pattern searcher) {
        return (int) splitter.matcher(content == null ? "" : content).results().filter(m -> searcher.matcher(m.group()).find()).count();
    }

    private int countContainingParagraphs(String content, Pattern searcher) {
        if (content == null || content.isBlank()) return 0;
        return (int) Arrays.stream(PARAGRAPH_SPLIT_PATTERN.split(content.strip())).filter(p -> searcher.matcher(p).find()).count();
    }

    private double calculateRelativeFrequency(int count, double total) {
        return total > 0 ? (count / total) * 1000.0 : 0.0;
    }

    private int sumComparisonCounts(TextComparisonRow row) {
        return row.countsByTextId().values().stream().mapToInt(Integer::intValue).sum();
    }

    private long sumMapValues(Map<String, Long> map) {
        return map.values().stream().mapToLong(Long::longValue).sum();
    }
}
