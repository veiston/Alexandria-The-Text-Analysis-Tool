package com.alexandria.model;

import com.alexandria.service.analysis.TermAnalysisResult;

import java.time.LocalDateTime;

public class ArchiveTermAnalysis {
	private Integer id;
	private Integer textId;
	private String textTitle;
	private String term;
	private LocalDateTime createdAt;
	private TermAnalysisResult termAnalysisResult;

	public ArchiveTermAnalysis() {
	}

	public ArchiveTermAnalysis(Integer id, Integer textId, String textTitle,
			String term, LocalDateTime createdAt, TermAnalysisResult termAnalysisResult) {
		this.id = id;
		this.textId = textId;
		this.textTitle = textTitle;
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

	public String getTextTitle() {
		return textTitle;
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
