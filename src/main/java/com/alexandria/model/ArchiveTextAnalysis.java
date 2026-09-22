package com.alexandria.model;

import java.time.LocalDateTime;
import com.alexandria.service.analysis.TextAnalysisResult;

public class ArchiveTextAnalysis {
	private Integer id;
	private Integer textId;
	private String projectTitle;
	private String sourceFileName;
	private LocalDateTime createdAt;
	private TextAnalysisResult textAnalysisResult;

	public ArchiveTextAnalysis() {
	}

	public ArchiveTextAnalysis(Integer id, Integer textId, String projectTitle,
			String sourceFileName, LocalDateTime createdAt, TextAnalysisResult textAnalysisResult) {
		this.id = id;
		this.textId = textId;
		this.projectTitle = projectTitle;
		this.sourceFileName = sourceFileName;
		this.createdAt = createdAt;
		this.textAnalysisResult = textAnalysisResult;
	}

	public Integer getId() {
		return id;
	}

	public Integer getTextId() {
		return textId;
	}

	public String getProjectTitle() {
		return projectTitle;
	}

	public String getSourceFileName() {
		return sourceFileName;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public TextAnalysisResult getTextAnalysisResult() {
		return textAnalysisResult;
	}
}
