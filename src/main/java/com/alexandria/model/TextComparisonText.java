package com.alexandria.model;

/**
 * Links a text comparison to a text it was done for.
 */
public class TextComparisonText {
	private Integer comparisonId;
	private Integer textId;

	public TextComparisonText() {
	}

	public TextComparisonText(Integer comparisonId, Integer textId) {
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
