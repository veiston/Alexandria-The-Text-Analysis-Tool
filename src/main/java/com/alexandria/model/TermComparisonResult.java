package com.alexandria.model;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

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
		occurrencesPerText = (occurrencesPerText != null) ? List.copyOf(occurrencesPerText) : Collections.emptyList();
	}

	/**
	 * Converts term comparison data to a JSON string matching the database schema.
	 *
	 * @return JSON string representation.
	 */
	public String toJson() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"term\":\"").append(escapeJson(term)).append("\",");
		sb.append("\"occurrencesPerText\":[");
		for (int i = 0; i < occurrencesPerText.size(); i++) {
			TermTextOccurrence occ = occurrencesPerText.get(i);
			sb.append("{")
				.append("\"textId\":").append(occ.textId()).append(",")
				.append("\"textTitle\":\"").append(escapeJson(occ.textTitle())).append("\",")
				.append("\"occurrences\":").append(occ.occurrences()).append(",")
				.append("\"relativeFrequency\":").append(String.format(Locale.US, "%.2f", occ.relativeFrequency()))
				.append("}");
			if (i < occurrencesPerText.size() - 1) {
				sb.append(",");
			}
		}
		sb.append("]");
		sb.append("}");
		return sb.toString();
	}

	private static String escapeJson(String input) {
		if (input == null) {
			return "";
		}
		return input.replace("\\", "\\\\")
			.replace("\"", "\\\"")
			.replace("\b", "\\b")
			.replace("\f", "\\f")
			.replace("\n", "\\n")
			.replace("\r", "\\r")
			.replace("\t", "\\t");
	}
}
