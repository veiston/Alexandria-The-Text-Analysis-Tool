package com.alexandria.service.analysis;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Service contract for multi-document comparison.
 * Computes shared vocabulary frequencies, pairwise Cosine Similarity scores,
 * similarity band classifications, and finds matching paragraph pairs.
 */
public interface TextComparisonServiceINT {

    /**
     * Compares multiple texts by calculating shared word frequencies, Cosine Similarity,
     * and similar paragraph excerpts.
     *
     * @param textsById map of document IDs to their text content
     * @param pageOffsetsById map of document IDs to page boundary offsets for PDF page resolution
     * @param limit maximum number of common-word rows to return (0 or negative for unlimited)
     * @return the comparison result containing shared word metrics, similarity score, and paragraph matches
     */
    TextComparisonResult compareTexts(Map<Integer, String> textsById, Map<Integer, List<Integer>> pageOffsetsById, int limit);

    /**
     * Convenience overload to compare multiple texts without requiring page offset information.
     *
     * @param textsById map of document IDs to their text content
     * @param limit maximum number of common-word rows to return (0 or negative for unlimited)
     * @return the comparison result containing shared word metrics, similarity score, and paragraph matches
     */
    default TextComparisonResult compareTexts(Map<Integer, String> textsById, int limit) {
        return compareTexts(textsById, Collections.emptyMap(), limit);
    }
}
