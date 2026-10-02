package com.alexandria.service;

import com.alexandria.service.analysis.*;

import java.util.*;
import java.util.regex.*;

import static com.alexandria.service.AnalysisUtils.*;

public class SearchService implements SearchServiceINT {

    @Override
    public List<SearchMatch> search(String content, String term, SearchSettings setting, List<Integer> pageOffsets) {
        if (content == null || content.isBlank() || term == null || term.isBlank()) {
            return Collections.emptyList();
        }

        if (setting == null) {
            setting = SearchSettings.defaults();
        }

        if (setting.ignoreStopWords() && STOP_WORDS.contains(term.toLowerCase(Locale.ROOT))) {
            return Collections.emptyList();
        }

        List<Integer> paragraphOffsets = resolveParagraphOffsets(content);

        if (setting.fuzzy()) {
            return new FuzzySearchService().findWithFuzzy(content, term, setting, pageOffsets, paragraphOffsets);
        }

        String query;
        if (setting.wholeWordsOnly()) {
            query = "\\b" + Pattern.quote(term) + "\\b";
        } else {
            query = Pattern.quote(term);
        }

        int flags;
        if (setting.caseSensitive()) {
            flags = Pattern.UNICODE_CASE;
        } else {
            flags = Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE;
        }

        var results = Pattern.compile(query, flags).matcher(content).results();
        if (setting.ignoreStopWords()) {
            results = results.filter(m -> !STOP_WORDS.contains(content.substring(m.start(), m.end()).toLowerCase(Locale.ROOT)));
        }

        return results
                .map(m -> {
                    String ctx = content
                            .substring(Math.max(0, m.start() - 40), Math.min(content.length(), m.end() + 40)).strip();
                    Integer page = resolvePage(m.start(), pageOffsets);
                    Integer paragraph = resolveParagraph(m.start(), paragraphOffsets);
                    return new SearchMatch(content.substring(m.start(), m.end()), m.start(), m.end(), page, paragraph, ctx);
                })
                .toList();
    }

    @Override
    public Map<Integer, List<SearchMatch>> searchMultiple(Map<Integer, String> contentsById, String term, SearchSettings settings, Map<Integer, List<Integer>> pageOffsetsById) {
        if (contentsById == null || contentsById.isEmpty()) {
            return Collections.emptyMap();
        }
        if (pageOffsetsById == null) {
            pageOffsetsById = Collections.emptyMap();
        }
        Map<Integer, List<SearchMatch>> results = new LinkedHashMap<>();
        for (Map.Entry<Integer, String> entry : contentsById.entrySet()) {
            int id = entry.getKey();
            results.put(id, search(entry.getValue(), term, settings, pageOffsetsById.getOrDefault(id, Collections.emptyList())));
        }
        return results;
    }
}
