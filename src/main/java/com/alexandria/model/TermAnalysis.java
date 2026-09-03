package com.alexandria.model;

import java.time.LocalDateTime;

/**
 * Represents saved analysis for a word or phrase in a text.
 */
public class TermAnalysis {
	private Integer id;
	private Integer userId;
	private Integer textId;
	private String term;
	private String analysisData;
	private LocalDateTime createdAt;

	public TermAnalysis() {
	}

	public TermAnalysis(Integer id, Integer userId, Integer textId, String term,
			String analysisData, LocalDateTime createdAt) {
		this.id = id;
		this.userId = userId;
		this.textId = textId;
		this.term = term;
		this.analysisData = analysisData;
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

	public Integer getTextId() {
		return textId;
	}

	public void setTextId(Integer textId) {
		this.textId = textId;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public String getAnalysisData() {
		return analysisData;
	}

	public void setAnalysisData(String analysisData) {
		this.analysisData = analysisData;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
}
