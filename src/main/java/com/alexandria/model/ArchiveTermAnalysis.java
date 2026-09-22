package com.alexandria.model;

import com.alexandria.service.analysis.TermAnalysisResult;

import java.time.LocalDateTime;

public class ArchiveTermAnalysis {
	private Integer id;
	private Integer textId;
	private String projectTitle;
	private String sourceFileName;
	private String term;
	private LocalDateTime createdAt;
	private TermAnalysisResult termAnalysisResult;

	public ArchiveTermAnalysis() {
	}

	public ArchiveTermAnalysis(Integer id, Integer textId, String projectTitle,
			String sourceFileName, String term, LocalDateTime createdAt, TermAnalysisResult termAnalysisResult) {
		this.id = id;
		this.textId = textId;
		this.projectTitle = projectTitle;
		this.sourceFileName = sourceFileName;
		this.term = term;
		this.createdAt = createdAt;
		this.termAnalysisResult = termAnalysisResult;
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

	public String getTerm() {
		return term;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public TermAnalysisResult getTermAnalysisResult() {
		return termAnalysisResult;
	}
}
