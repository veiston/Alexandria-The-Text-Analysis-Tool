package com.alexandria.model;

/**
 * Links a term comparison to a text it was done for.
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
