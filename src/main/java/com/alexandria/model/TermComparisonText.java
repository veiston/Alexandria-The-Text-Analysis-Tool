package com.alexandria.model;

/**
 * Text Comparison with entered word/phrase junction.
 *
 * A term comparison can include several texts. This model stores one link
 * between a saved term comparison and each text included in it.
 */
public class TermComparisonText {
	private Integer comparisonId;
	private Integer textId;

	public TermComparisonText() {
	}

	public TermComparisonText(Integer comparisonId, Integer textId) {
		this.comparisonId = comparisonId;
		this.textId = textId;
	}

	public Integer getComparisonId() {
		return comparisonId;
	}

	public void setComparisonId(Integer comparisonId) {
		this.comparisonId = comparisonId;
	}

	public Integer getTextId() {
		return textId;
	}

	public void setTextId(Integer textId) {
		this.textId = textId;
	}
}
