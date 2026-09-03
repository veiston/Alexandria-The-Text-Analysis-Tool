package com.alexandria.model;

import java.time.LocalDateTime;

/**
 * Represents saved analysis for a text.
 */
public class TextAnalysis {
	private Integer id;
	private Integer userId;
	private Integer textId;
	private String analysisData;
	private LocalDateTime createdAt;

	public TextAnalysis() {
	}

	public TextAnalysis(Integer userId, Integer textId, String analysisData) {
		this.userId = userId;
		this.textId = textId;
		this.analysisData = analysisData;
	}

	public TextAnalysis(Integer id, Integer userId, Integer textId,
			String analysisData, LocalDateTime createdAt) {
		this.id = id;
		this.userId = userId;
		this.textId = textId;
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
