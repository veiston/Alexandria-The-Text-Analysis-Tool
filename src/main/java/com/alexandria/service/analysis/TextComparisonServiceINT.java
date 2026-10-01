package com.alexandria.service.analysis;

import java.util.Map;

public interface TextComparisonServiceINT {
    TextComparisonResult compareTexts(Map<Integer, String> textsById, int limit);
}
