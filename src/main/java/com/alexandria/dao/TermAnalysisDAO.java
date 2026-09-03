package com.alexandria.dao;

import com.alexandria.db.DatabaseConnection;
import com.alexandria.model.TermAnalysis;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Text Analysis with entered word/phrase.
 *
 * Manages analysis for a word or phrase entered by the user in one text:
 * total occurrences, relative frequency, sentences and paragraphs containing it,
 * and common words near it.
 */
public class TermAnalysisDAO {
	public TermAnalysis create(TermAnalysis analysis) throws SQLException {
		String sql = "INSERT INTO term_analysis (user_id, text_id, term, analysis_data) VALUES (?, ?, ?, ?)";

		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			statement.setInt(1, analysis.getUserId());
			statement.setInt(2, analysis.getTextId());
			statement.setString(3, analysis.getTerm());
			statement.setString(4, analysis.getAnalysisData());

			statement.executeUpdate();

			try (ResultSet keys = statement.getGeneratedKeys()) {
				if (keys.next()) {
					analysis.setId(keys.getInt(1));
				}
			}
		}

		return analysis;
	}

	public TermAnalysis findById(int id) throws SQLException {
		String sql = "SELECT * FROM term_analysis WHERE id = ?";

		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, id);

			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					return new TermAnalysis(
							resultSet.getInt("id"),
							resultSet.getInt("user_id"),
							resultSet.getInt("text_id"),
							resultSet.getString("term"),
							resultSet.getString("analysis_data"),
							resultSet.getTimestamp("created_at").toLocalDateTime());
				}
			}
		}

		return null;
	}

	public List<TermAnalysis> findAllByUserId(int userId) throws SQLException {
		String sql = "SELECT * FROM term_analysis WHERE user_id = ?";
		List<TermAnalysis> analyses = new ArrayList<>();

		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, userId);

			try (ResultSet resultSet = statement.executeQuery()) {
				while (resultSet.next()) {
					TermAnalysis analysis = new TermAnalysis(
							resultSet.getInt("id"),
							resultSet.getInt("user_id"),
							resultSet.getInt("text_id"),
							resultSet.getString("term"),
							resultSet.getString("analysis_data"),
							resultSet.getTimestamp("created_at").toLocalDateTime());

					analyses.add(analysis);
				}
			}
		}

		return analyses;
	}

	public List<TermAnalysis> findAllByTextId(int textId) throws SQLException {
		String sql = "SELECT * FROM term_analysis WHERE text_id = ?";
		List<TermAnalysis> analyses = new ArrayList<>();

		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, textId);

			try (ResultSet resultSet = statement.executeQuery()) {
				while (resultSet.next()) {
					TermAnalysis analysis = new TermAnalysis(
							resultSet.getInt("id"),
							resultSet.getInt("user_id"),
							resultSet.getInt("text_id"),
							resultSet.getString("term"),
							resultSet.getString("analysis_data"),
							resultSet.getTimestamp("created_at").toLocalDateTime());

					analyses.add(analysis);
				}
			}
		}

		return analyses;
	}

	public boolean update(TermAnalysis analysis) throws SQLException {
		String sql = "UPDATE term_analysis SET term = ?, analysis_data = ? WHERE id = ?";

		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, analysis.getTerm());
			statement.setString(2, analysis.getAnalysisData());
			statement.setInt(3, analysis.getId());

			int updatedRows = statement.executeUpdate();

			return updatedRows > 0;
		}
	}

	public boolean delete(int id) throws SQLException {
		String sql = "DELETE FROM term_analysis WHERE id = ?";

		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, id);

			int deletedRows = statement.executeUpdate();

			return deletedRows > 0;
		}
	}
}
