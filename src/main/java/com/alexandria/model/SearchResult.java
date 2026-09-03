package com.alexandria.model;

import java.time.LocalDateTime;

/**
 * Text Search.
 *
 * Stores results of a search for a word or phrase entered by the user.
 * Search settings are selected by the user.
 */
public class SearchResult {
	private Integer id;
	private Integer userId;
	private Integer textId;
	private String query;
	private String resultsData;
	private LocalDateTime createdAt;

	public SearchResult() {
	}

	public SearchResult(Integer id, Integer userId, Integer textId, String query,
			String resultsData, LocalDateTime createdAt) {
		this.id = id;
		this.userId = userId;
		this.textId = textId;
		this.query = query;
		this.resultsData = resultsData;
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

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getResultsData() {
		return resultsData;
	}

	public void setResultsData(String resultsData) {
		this.resultsData = resultsData;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
}
