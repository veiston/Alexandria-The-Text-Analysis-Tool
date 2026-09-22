package com.alexandria.service.analysis;

import java.util.List;

public interface TextAnalysisServiceINT {
    TextAnalysisResult analyzeText(String content, List<Integer> pageOffsets);
}
