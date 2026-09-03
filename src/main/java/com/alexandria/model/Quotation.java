package com.alexandria.model;

import java.time.LocalDateTime;

/**
 * Represents a quotation saved from a text.
 */
public class Quotation {
	private Integer id;
	private Integer userId;
	private Integer textId;
	private String quotationText;
	private String location;
	private LocalDateTime createdAt;

	public Quotation() {
	}

	public Quotation(Integer id, Integer userId, Integer textId, String quotationText,
			String location, LocalDateTime createdAt) {
		this.id = id;
		this.userId = userId;
		this.textId = textId;
		this.quotationText = quotationText;
		this.location = location;
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

	public String getQuotationText() {
		return quotationText;
	}

	public void setQuotationText(String quotationText) {
		this.quotationText = quotationText;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
}
