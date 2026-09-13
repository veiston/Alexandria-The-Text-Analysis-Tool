package com.alexandria.service;

import com.alexandria.service.analysis.*;

import java.util.*;
import java.util.regex.*;

import static com.alexandria.service.AnalysisUtils.*;

public class SearchService implements SearchServiceINT {

    @Override
    public List<SearchMatch> search(String content, String term, SearchSettings setting) {
        return search(content, term, setting, Collections.emptyList());
    }

    public List<SearchMatch> search(String content, String term, SearchSettings setting, List<Integer> pageOffsets) {
        if (content == null || content.isBlank() || term == null || term.isBlank()) return Collections.emptyList();

        if (setting.fuzzy()) {
            return new FuzzySearchService().findWithFuzzy(content, term);
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

        return Pattern.compile(query, flags).matcher(content).results()
                .map(m -> {
                    String ctx = content.substring(Math.max(0, m.start() - 40), Math.min(content.length(), m.end() + 40)).strip();
                    Integer page = resolvePage(m.start(), pageOffsets);
                    return new SearchMatch(content.substring(m.start(), m.end()), m.start(), m.end(), page, null, ctx);
                })
                .toList();
    }
}
