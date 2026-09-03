package com.alexandria.model;

import java.time.LocalDateTime;

/**
 * Represents a saved comparison result for several texts.
 */
public class TextComparison {
	private Integer id;
	private Integer userId;
	private String comparisonData;
	private LocalDateTime createdAt;

	public TextComparison() {
	}

	public TextComparison(Integer userId, String comparisonData) {
		this.userId = userId;
		this.comparisonData = comparisonData;
	}

	public TextComparison(Integer id, Integer userId, String comparisonData,
			LocalDateTime createdAt) {
		this.id = id;
		this.userId = userId;
		this.comparisonData = comparisonData;
		this.createdAt = createdAt;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getComparisonData() {
		return comparisonData;
	}

	public void setComparisonData(String comparisonData) {
		this.comparisonData = comparisonData;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
}
