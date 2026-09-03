package com.alexandria.model;

import java.time.LocalDateTime;

/**
 * Represents saved analysis for a text.
 */
public class TextAnalysis {
	private Integer id;
	private Integer userId;
	private Integer textId;
	private String statisticsData;
	private LocalDateTime createdAt;

	public TextAnalysis() {
	}

	public TextAnalysis(Integer id, Integer userId, Integer textId,
			String statisticsData, LocalDateTime createdAt) {
		this.id = id;
		this.userId = userId;
		this.textId = textId;
		this.statisticsData = statisticsData;
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

	public String getStatisticsData() {
		return statisticsData;
	}

	public void setStatisticsData(String statisticsData) {
		this.statisticsData = statisticsData;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
}
