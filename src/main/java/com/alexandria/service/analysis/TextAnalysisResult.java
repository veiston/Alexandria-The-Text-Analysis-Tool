package com.alexandria.service.analysis;

import java.util.List;

/**
 * Contains automatically calculated statistics and important fragments
 * for a text.
 *
 * @param totalWords the total number of words in the text
 * @param uniqueWords the number of unique words after normalization
 *                    and removal of stop words
 * @param totalSentences the total number of sentences detected in the text
 * @param totalParagraphs the total number of paragraphs detected in the text
 * @param frequentWords the most frequently used non-stop words,
 *                      limited to the top 5
 * @param importantFragments the text fragments with the highest scores
 *                           based on the number of frequent words they contain,
 *                           limited to the top 5
 */
public record TextAnalysisResult(
        int totalWords,
        int uniqueWords,
        int totalSentences,
        int totalParagraphs,
        List<WordFrequency> frequentWords,
        List<TextFragment> importantFragments) {}

