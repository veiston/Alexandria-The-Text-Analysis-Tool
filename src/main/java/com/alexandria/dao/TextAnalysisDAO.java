package com.alexandria.dao;

import com.alexandria.db.DatabaseConnection;
import com.alexandria.model.TextAnalysis;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Text Analysis.
 *
 * Manages analysis for one text: total words, unique words, sentences,
 * paragraphs, most frequently used words, and important text fragments.
 */
public class TextAnalysisDAO {
	public TextAnalysis create(TextAnalysis textAnalysis) throws SQLException {
		String sql = "INSERT INTO text_analysis (user_id, text_id, analysis_data) VALUES (?, ?, ?)";

		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			statement.setInt(1, textAnalysis.getUserId());
			statement.setInt(2, textAnalysis.getTextId());
			statement.setString(3, textAnalysis.getAnalysisData());
			
			statement.executeUpdate();

			try (ResultSet keys = statement.getGeneratedKeys()) {
				if (keys.next()) {
					textAnalysis.setId(keys.getInt(1));
				}
			}
		}

		return textAnalysis;
	}

	public TextAnalysis findById(int id) throws SQLException {
		String sql = "SELECT * FROM text_analysis WHERE id = ?";

		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, id);

			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					return new TextAnalysis(
							resultSet.getInt("id"),
							resultSet.getInt("user_id"),
							resultSet.getInt("text_id"),
							resultSet.getString("analysis_data"),
							resultSet.getTimestamp("created_at").toLocalDateTime());
				}
			}
		}

		return null;
	}

	public List<TextAnalysis> findAllByUserId(int userId) throws SQLException {
		String sql = "SELECT * FROM text_analysis WHERE user_id = ?";
		List<TextAnalysis> textAnalyses = new ArrayList<>();

		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, userId);

			try (ResultSet resultSet = statement.executeQuery()) {
				while (resultSet.next()) {
					TextAnalysis textAnalysis = new TextAnalysis(
							resultSet.getInt("id"),
							resultSet.getInt("user_id"),
							resultSet.getInt("text_id"),
							resultSet.getString("analysis_data"),
							resultSet.getTimestamp("created_at").toLocalDateTime());

					textAnalyses.add(textAnalysis);
				}
			}
		}

		return textAnalyses;
	}

	public List<TextAnalysis> findAllByTextId(int textId) throws SQLException {
		String sql = "SELECT * FROM text_analysis WHERE text_id = ?";
		List<TextAnalysis> textAnalyses = new ArrayList<>();

		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, textId);

			try (ResultSet resultSet = statement.executeQuery()) {
				while (resultSet.next()) {
					TextAnalysis textAnalysis = new TextAnalysis(
							resultSet.getInt("id"),
							resultSet.getInt("user_id"),
							resultSet.getInt("text_id"),
							resultSet.getString("analysis_data"),
							resultSet.getTimestamp("created_at").toLocalDateTime());

					textAnalyses.add(textAnalysis);
				}
			}
		}

		return textAnalyses;
	}

	public boolean update(TextAnalysis textAnalysis) throws SQLException {
		String sql = "UPDATE text_analysis SET analysis_data = ? WHERE id = ?";

		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, textAnalysis.getAnalysisData());
			statement.setInt(2, textAnalysis.getId());

			int updatedRows = statement.executeUpdate();

			return updatedRows > 0;
		}
	}

	public boolean delete(int id) throws SQLException {
		String sql = "DELETE FROM text_analysis WHERE id = ?";

		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, id);

			int deletedRows = statement.executeUpdate();

			return deletedRows > 0;
		}
	}
}
