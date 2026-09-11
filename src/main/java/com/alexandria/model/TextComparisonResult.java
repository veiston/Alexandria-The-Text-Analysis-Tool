package com.alexandria.model;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
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
			countsByTextId = (countsByTextId != null) ? Map.copyOf(countsByTextId) : Collections.emptyMap();
			relativeFreqByTextId = (relativeFreqByTextId != null) ? Map.copyOf(relativeFreqByTextId) : Collections.emptyMap();
		}
	}

	public TextComparisonResult {
		textIds = (textIds != null) ? List.copyOf(textIds) : Collections.emptyList();
		commonWords = (commonWords != null) ? List.copyOf(commonWords) : Collections.emptyList();
	}

	/**
	 * Converts text comparison data to a JSON string that works with our database schema.
	 *
	 * @return JSON string representation.
	 */
	public String toJson() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"textIds\":[");
		for (int i = 0; i < textIds.size(); i++) {
			sb.append(textIds.get(i));
			if (i < textIds.size() - 1) {
				sb.append(",");
			}
		}
		sb.append("],");

		sb.append("\"commonWords\":[");
		for (int i = 0; i < commonWords.size(); i++) {
			TextComparisonRow row = commonWords.get(i);
			sb.append("{")
				.append("\"word\":\"").append(escapeJson(row.word())).append("\",")
				.append("\"counts\":{");

			int countIndex = 0;
			for (Map.Entry<Integer, Integer> entry : row.countsByTextId().entrySet()) {
				sb.append("\"").append(entry.getKey()).append("\":").append(entry.getValue());
				if (++countIndex < row.countsByTextId().size()) {
					sb.append(",");
				}
			}
			sb.append("},");

			sb.append("\"relativeFrequencies\":{");
			int freqIndex = 0;
			for (Map.Entry<Integer, Double> entry : row.relativeFreqByTextId().entrySet()) {
				sb.append("\"").append(entry.getKey()).append("\":")
					.append(String.format(Locale.US, "%.2f", entry.getValue()));
				if (++freqIndex < row.relativeFreqByTextId().size()) {
					sb.append(",");
				}
			}
			sb.append("}");

			sb.append("}");
			if (i < commonWords.size() - 1) {
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
