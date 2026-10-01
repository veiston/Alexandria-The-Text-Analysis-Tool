package com.alexandria.service.analysis;

import java.util.List;
import java.util.Map;

public interface TextComparisonServiceINT {
    TextComparisonResult compareTexts(Map<Integer, String> textsById, Map<Integer, List<Integer>> pageOffsetsById, int limit);
}
