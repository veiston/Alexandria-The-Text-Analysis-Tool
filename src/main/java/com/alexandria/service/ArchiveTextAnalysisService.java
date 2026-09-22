package com.alexandria.service;

import com.alexandria.dao.TextAnalysisDAO;
import com.alexandria.dao.TextDAO;
import com.alexandria.model.ArchiveTextAnalysis;
import com.alexandria.model.Text;
import com.alexandria.model.TextAnalysis;
import com.alexandria.service.analysis.TextAnalysisResult;
import com.alexandria.utils.JsonMapper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ArchiveTextAnalysisService {
	private final TextAnalysisDAO textAnalysisDAO;
	private final TextDAO textDAO;

	public ArchiveTextAnalysisService() {
		this(new TextAnalysisDAO(), new TextDAO());
	}

	ArchiveTextAnalysisService(TextAnalysisDAO textAnalysisDAO, TextDAO textDAO) {
		this.textAnalysisDAO = textAnalysisDAO;
		this.textDAO = textDAO;
	}

	ArchiveTextAnalysisService(TextAnalysisDAO textAnalysisDAO) {
		this(textAnalysisDAO, new TextDAO());
	}

	public TextAnalysis save(Text text, TextAnalysisResult textAnalysisResult) throws SQLException {
		boolean hasUserId = text != null && text.getUserId() != null;
		boolean hasTextId = text != null && text.getId() != null;
		boolean hasTextAnalysisResult = textAnalysisResult != null;

		if (!hasUserId || !hasTextId) {
			throw new IllegalArgumentException("A saved text belonging to a user is required.");
		}

		if (!hasTextAnalysisResult) {
			throw new IllegalArgumentException("Text analysis result is required.");
		}

		TextAnalysis textAnalysis = new TextAnalysis(text.getUserId(), text.getId(), JsonMapper.toJson(textAnalysisResult));
		
		TextAnalysis saveTextAnalysis = textAnalysisDAO.create(textAnalysis);

		return saveTextAnalysis;
	}

	public ArchiveTextAnalysis findById(int id, int userId) throws SQLException {
		TextAnalysis textAnalysis = textAnalysisDAO.findById(id);

		if (textAnalysis == null) {
			throw new IllegalArgumentException("Text analysis with the provided ID was not found.");
		}

		if (!Integer.valueOf(userId).equals(textAnalysis.getUserId())) {
			throw new IllegalArgumentException("Text analysis does not belong to the provided user.");
		}

		Text text = textDAO.findById(textAnalysis.getTextId());

		if (text == null) {
			throw new IllegalStateException("Text for the saved analysis was not found.");
		}

		TextAnalysisResult textAnalysisResult = JsonMapper.fromJson(textAnalysis.getAnalysisData(), TextAnalysisResult.class);

		return new ArchiveTextAnalysis(
				textAnalysis.getId(),
				textAnalysis.getTextId(),
				text.getTitle(),
				text.getFileName(),
				textAnalysis.getCreatedAt(),
				textAnalysisResult);
	}

	public List<ArchiveTextAnalysis> findAllByUserId(int userId) throws SQLException {
		List<TextAnalysis> textAnalyses = textAnalysisDAO.findAllByUserId(userId);
		List<ArchiveTextAnalysis> archiveTextAnalyses = new ArrayList<>();

		for (TextAnalysis textAnalysis : textAnalyses) {
			Text text = textDAO.findById(textAnalysis.getTextId());

			if (text == null) {
				throw new IllegalStateException("Text for the saved analysis was not found.");
			}

			TextAnalysisResult textAnalysisResult = JsonMapper.fromJson(textAnalysis.getAnalysisData(), TextAnalysisResult.class);

			ArchiveTextAnalysis archiveTextAnalysis = new ArchiveTextAnalysis(
					textAnalysis.getId(),
					textAnalysis.getTextId(),
					text.getTitle(),
					text.getFileName(),
					textAnalysis.getCreatedAt(),
					textAnalysisResult);

			archiveTextAnalyses.add(archiveTextAnalysis);
		}

		return archiveTextAnalyses;
	}

	public List<ArchiveTextAnalysis> findAllByTextId(int textId, int userId) throws SQLException {
		Text text = textDAO.findById(textId);

		if (text == null) {
			throw new IllegalArgumentException("Text with the provided ID was not found.");
		}

		if (!Integer.valueOf(userId).equals(text.getUserId())) {
			throw new IllegalArgumentException("Text does not belong to the provided user.");
		}

		List<TextAnalysis> textAnalyses = textAnalysisDAO.findAllByTextId(textId);
		List<ArchiveTextAnalysis> archiveTextAnalyses = new ArrayList<>();

		for (TextAnalysis textAnalysis : textAnalyses) {
			if (!Integer.valueOf(userId).equals(textAnalysis.getUserId())) {
				throw new IllegalArgumentException("Text analysis does not belong to the provided user.");
			}

			TextAnalysisResult textAnalysisResult = JsonMapper.fromJson(textAnalysis.getAnalysisData(), TextAnalysisResult.class);

			ArchiveTextAnalysis archiveTextAnalysis = new ArchiveTextAnalysis(
					textAnalysis.getId(),
					textAnalysis.getTextId(),
					text.getTitle(),
					text.getFileName(),
					textAnalysis.getCreatedAt(),
					textAnalysisResult);

			archiveTextAnalyses.add(archiveTextAnalysis);
		}

		return archiveTextAnalyses;
	}

	public boolean deleteById(Integer id, int userId) throws SQLException {
		TextAnalysis textAnalysis = textAnalysisDAO.findById(id);

		if (textAnalysis == null) {
			throw new IllegalArgumentException("Text analysis with the provided ID was not found.");
		}

		if (!Integer.valueOf(userId).equals(textAnalysis.getUserId())) {
			throw new IllegalArgumentException("Text analysis does not belong to the provided user.");
		}

		return textAnalysisDAO.delete(id);
	}
}
