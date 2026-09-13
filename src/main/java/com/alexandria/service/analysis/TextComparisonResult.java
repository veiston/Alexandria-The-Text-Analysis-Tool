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
	List<TextComparisonRow> commonWords
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
	}
}
