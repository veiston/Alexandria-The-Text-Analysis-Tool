package com.alexandria.service.analysis;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Text Comparison Result.
 *
 * Stores comparative statistical analysis across multiple texts:
 * list of compared text IDs and common frequent words with their raw and
 * relative occurrence counts in each text.
 */
public record TextComparisonResult(
	List<Integer> textIds,
	List<TextComparisonRow> commonWords,
	double similarityScore,
	String similarityBand,
	List<ParagraphMatch> similarParagraphs
) {
	public record TextComparisonRow(
		String word,
		Map<Integer, Integer> countsByTextId,
		Map<Integer, Double> relativeFreqByTextId
	) {
		public TextComparisonRow {
			if (word == null || word.isBlank()) {
				throw new IllegalArgumentException("Word cannot be null or blank");
			}
			if (countsByTextId != null) {
				countsByTextId = Map.copyOf(countsByTextId);
			} else {
				countsByTextId = Collections.emptyMap();
			}
			if (relativeFreqByTextId != null) {
				relativeFreqByTextId = Map.copyOf(relativeFreqByTextId);
			} else {
				relativeFreqByTextId = Collections.emptyMap();
			}
		}
	}

	public enum SimilarityBand {
		LOW("Low"),
		MODERATE("Moderate"),
		HIGH("High");

		private final String label;

		SimilarityBand(String label) {
			this.label = label;
		}

		public String label() {
			return label;
		}

			// These can be optimized later
		public static SimilarityBand fromScore(double score) {
			if (score >= 67.0) {
				return HIGH;
			}
			if (score >= 34.0) {
				return MODERATE;
			}
			return LOW;
		}
	}

	public record ParagraphSnippet(
		String text,
		int charStart,
		int charEnd,
		Integer page,
		int paragraphIndex
	) {}

	public record ParagraphMatch(
		ParagraphSnippet first,
		ParagraphSnippet second,
		double scorePercent
	) {}

	public TextComparisonResult {
		if (textIds != null) {
			textIds = List.copyOf(textIds);
		} else {
			textIds = Collections.emptyList();
		}
		if (commonWords != null) {
			commonWords = List.copyOf(commonWords);
		} else {
			commonWords = Collections.emptyList();
		}
		if (similarParagraphs != null) {
			similarParagraphs = List.copyOf(similarParagraphs);
		} else {
			similarParagraphs = Collections.emptyList();
		}
	}
}
