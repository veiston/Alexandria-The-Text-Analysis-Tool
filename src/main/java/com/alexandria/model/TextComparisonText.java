package com.alexandria.model;

/**
 * Text Comparison junction.
 *
 * A text comparison can include several texts. This model stores one link
 * between a saved text comparison and each text included in it.
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
