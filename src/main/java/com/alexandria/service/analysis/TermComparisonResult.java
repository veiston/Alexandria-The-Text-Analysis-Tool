package com.alexandria.service.analysis;

import java.util.Collections;
import java.util.List;

/**
 * "Term Comparison Result" tool
 *
 * Stores comparison for a specific word/phrase across multiple texts:
 * the term, and the occurrences and relative frequencies for each text.
 */
public record TermComparisonResult(
	String term,
	List<TermTextOccurrence> occurrencesPerText
) {
	public record TermTextOccurrence(
		int textId,
		String textTitle,
		int occurrences,
		double relativeFrequency
	) {
		public TermTextOccurrence {
			if (occurrences < 0 || relativeFrequency < 0.0) {
				throw new IllegalArgumentException("Counts cannot possibly be negative!");
			}
			if (textTitle == null) {
				textTitle = "Text " + textId;
			}
		}
	}

	public TermComparisonResult {
		if (term == null || term.isBlank()) {
			throw new IllegalArgumentException("Term cannot be empty!");
		}
		if (occurrencesPerText != null) {
			occurrencesPerText = List.copyOf(occurrencesPerText);
		} else {
			occurrencesPerText = Collections.emptyList();
		}
	}
}
