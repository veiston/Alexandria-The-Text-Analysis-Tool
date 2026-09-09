package com.alexandria.service.analysis;

import java.util.List;

/**
 * Contains statistics about occurrences of a specific word or phrase
 * within a text.
 *
 * @param term the word or phrase that was analyzed
 * @param totalOccurrences the total number of times the term occurs in the text
 * @param relativeFrequency the number of occurrences per 1,000 words,
 *                          calculated as totalOccurrences / totalWords * 1,000
 * @param sentenceCount the number of different sentences containing the term
 * @param paragraphCount the number of different paragraphs containing the term
 * @param neighboringWords the most common words found near occurrences of the term,
 *                         limited to the top 5
 */
public record TermAnalysisResult(
        String term,
        int totalOccurrences,
        double relativeFrequency,
        int sentenceCount,
        int paragraphCount,
        List<WordFrequency> neighboringWords) {}
