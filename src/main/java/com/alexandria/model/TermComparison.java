package com.alexandria.model;

import java.time.LocalDateTime;

/**
 * Represents a saved term comparison result for several texts.
 */
public class TermComparison {
	private Integer id;
	private Integer userId;
	private String term;
	private String comparisonData;
	private LocalDateTime createdAt;

	public TermComparison() {
	}

	public TermComparison(Integer id, Integer userId, String term,
			String comparisonData, LocalDateTime createdAt) {
		this.id = id;
		this.userId = userId;
		this.term = term;
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

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
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
