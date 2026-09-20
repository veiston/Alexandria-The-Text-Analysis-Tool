package com.alexandria.model;

import java.time.LocalDateTime;
import com.alexandria.service.analysis.TextAnalysisResult;

public class ArchiveTextAnalysis {
	private Integer id;
	private Integer textId;
	private String textTitle;
	private LocalDateTime createdAt;
	private TextAnalysisResult textAnalysisResult;

	public ArchiveTextAnalysis() {
	}

	public ArchiveTextAnalysis(Integer id, Integer textId, String textTitle, LocalDateTime createdAt, TextAnalysisResult textAnalysisResult) {
		this.id = id;
		this.textId = textId;
		this.textTitle = textTitle;
		this.createdAt = createdAt;
		this.textAnalysisResult = textAnalysisResult;
	}

	public Integer getId() {
		return id;
	}

	public Integer getTextId() {
		return textId;
	}

	public String getTextTitle() {
		return textTitle;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public TextAnalysisResult getTextAnalysisResult() {
		return textAnalysisResult;
	}
}
