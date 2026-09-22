package com.alexandria.service;

import com.alexandria.dao.TermAnalysisDAO;
import com.alexandria.dao.TextDAO;
import com.alexandria.model.ArchiveTermAnalysis;
import com.alexandria.model.TermAnalysis;
import com.alexandria.model.Text;
import com.alexandria.service.analysis.TermAnalysisResult;
import com.alexandria.utils.JsonMapper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArchiveTermAnalysisService {
	private final TermAnalysisDAO termAnalysisDAO;
	private final TextDAO textDAO;

	public ArchiveTermAnalysisService() {
		this(new TermAnalysisDAO(), new TextDAO());
	}

	ArchiveTermAnalysisService(TermAnalysisDAO termAnalysisDAO, TextDAO textDAO) {
		this.termAnalysisDAO = termAnalysisDAO;
		this.textDAO = textDAO;
	}

	ArchiveTermAnalysisService(TermAnalysisDAO termAnalysisDAO) {
		this(termAnalysisDAO, new TextDAO());
	}

	public TermAnalysis save(Text text, TermAnalysisResult termAnalysisResult) throws SQLException {
		boolean hasUserId = text != null && text.getUserId() != null;
		boolean hasTextId = text != null && text.getId() != null;
		boolean hasTermAnalysisResult = termAnalysisResult != null;
		boolean hasTerm = hasTermAnalysisResult
				&& termAnalysisResult.term() != null
				&& !termAnalysisResult.term().isBlank();

		if (!hasUserId || !hasTextId) {
			throw new IllegalArgumentException("A saved text belonging to a user is required.");
		}

		if (!hasTermAnalysisResult || !hasTerm) {
			throw new IllegalArgumentException("Term analysis result with a term is required.");
		}

		TermAnalysis termAnalysis = new TermAnalysis(text.getUserId(), text.getId(), termAnalysisResult.term(), JsonMapper.toJson(termAnalysisResult));
		TermAnalysis savedTermAnalysis = termAnalysisDAO.create(termAnalysis);

		return savedTermAnalysis;
	}

	public ArchiveTermAnalysis findById(int id, int userId) throws SQLException {
		TermAnalysis termAnalysis = termAnalysisDAO.findById(id);

		if (termAnalysis == null) {
			throw new IllegalArgumentException("Term analysis with the provided ID was not found.");
		}

		if (!Integer.valueOf(userId).equals(termAnalysis.getUserId())) {
			throw new IllegalArgumentException("Term analysis does not belong to the provided user.");
		}

		Text text = textDAO.findById(termAnalysis.getTextId());

		if (text == null) {
			throw new IllegalStateException("Text for the saved analysis was not found.");
		}

		TermAnalysisResult termAnalysisResult = JsonMapper.fromJson(termAnalysis.getAnalysisData(), TermAnalysisResult.class);

		return new ArchiveTermAnalysis(
				termAnalysis.getId(),
				termAnalysis.getTextId(),
				text.getTitle(),
				text.getFileName(),
				termAnalysis.getTerm(),
				termAnalysis.getCreatedAt(),
				termAnalysisResult);
	}

	public List<ArchiveTermAnalysis> findAllByUserId(int userId) throws SQLException {
		List<TermAnalysis> termAnalyses = termAnalysisDAO.findAllByUserId(userId);
		List<Text> texts = textDAO.findAllByUserId(userId);
		Map<Integer, Text> textsById = new HashMap<>();

		for (Text text : texts) {
			textsById.put(text.getId(), text);
		}

		List<ArchiveTermAnalysis> archiveTermAnalyses = new ArrayList<>();

		for (TermAnalysis termAnalysis : termAnalyses) {
			Text text = textsById.get(termAnalysis.getTextId());

			if (text == null) {
				throw new IllegalStateException("Text for the saved analysis was not found.");
			}

			TermAnalysisResult termAnalysisResult = JsonMapper.fromJson(termAnalysis.getAnalysisData(), TermAnalysisResult.class);

			ArchiveTermAnalysis archiveTermAnalysis = new ArchiveTermAnalysis(
					termAnalysis.getId(),
					termAnalysis.getTextId(),
					text.getTitle(),
					text.getFileName(),
					termAnalysis.getTerm(),
					termAnalysis.getCreatedAt(),
					termAnalysisResult);

			archiveTermAnalyses.add(archiveTermAnalysis);
		}

		return archiveTermAnalyses;
	}

	public List<ArchiveTermAnalysis> findAllByTextId(int textId, int userId) throws SQLException {
		Text text = textDAO.findById(textId);

		if (text == null) {
			throw new IllegalArgumentException("Text with the provided ID was not found.");
		}

		if (!Integer.valueOf(userId).equals(text.getUserId())) {
			throw new IllegalArgumentException("Text does not belong to the provided user.");
		}

		List<TermAnalysis> termAnalyses = termAnalysisDAO.findAllByTextId(textId);
		List<ArchiveTermAnalysis> archiveTermAnalyses = new ArrayList<>();

		for (TermAnalysis termAnalysis : termAnalyses) {
			if (!Integer.valueOf(userId).equals(termAnalysis.getUserId())) {
				throw new IllegalArgumentException("Term analysis does not belong to the provided user.");
			}

			TermAnalysisResult termAnalysisResult = JsonMapper.fromJson(termAnalysis.getAnalysisData(), TermAnalysisResult.class);

			ArchiveTermAnalysis archiveTermAnalysis = new ArchiveTermAnalysis(
					termAnalysis.getId(),
					termAnalysis.getTextId(),
					text.getTitle(),
					text.getFileName(),
					termAnalysis.getTerm(),
					termAnalysis.getCreatedAt(),
					termAnalysisResult);

			archiveTermAnalyses.add(archiveTermAnalysis);
		}

		return archiveTermAnalyses;
	}

	public boolean deleteById(Integer id, int userId) throws SQLException {
		TermAnalysis termAnalysis = termAnalysisDAO.findById(id);

		if (termAnalysis == null) {
			throw new IllegalArgumentException("Term analysis with the provided ID was not found.");
		}

		if (!Integer.valueOf(userId).equals(termAnalysis.getUserId())) {
			throw new IllegalArgumentException("Term analysis does not belong to the provided user.");
		}

		return termAnalysisDAO.delete(id);
	}
}
