package com.alexandria.service.analysis;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public interface SearchServiceINT {
    List<SearchMatch> search(String content, String term, SearchSettings setting, List<Integer> pageOffsets);
    Map<Integer, List<SearchMatch>> searchMultiple(Map<Integer, String> contentsById, String term, SearchSettings settings, Map<Integer, List<Integer>> pageOffsetsById);

    default Map<Integer, List<SearchMatch>> searchMultiple(Map<Integer, String> contentsById, String term, SearchSettings settings) {
        return searchMultiple(contentsById, term, settings, Collections.emptyMap());
    }
}