package com.alexandria.service.analysis;

import java.util.List;

public interface SearchServiceINT {
    List<SearchMatch> search(String content, String term, SearchSettings setting, List<Integer> pageOffsets);
}