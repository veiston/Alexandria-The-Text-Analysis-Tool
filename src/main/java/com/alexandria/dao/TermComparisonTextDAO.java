package com.alexandria.dao;

import com.alexandria.db.DatabaseConnection;
import com.alexandria.model.TermComparisonText;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Term Comparison Texts.
 *
 * Links a term comparison to the texts included in it.
 * Each row contains one comparison ID and one text ID.
 * TODO: Use this DAO in the service to save and load texts for a term comparison.
 */
public class TermComparisonTextDAO {
	public boolean create(TermComparisonText termComparisonText) throws SQLException {
		String sql = "INSERT INTO term_comparison_texts (comparison_id, text_id) VALUES (?, ?)";

		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, termComparisonText.getComparisonId());
			statement.setInt(2, termComparisonText.getTextId());

			int createdRows = statement.executeUpdate();

			return createdRows > 0;
		}
	}

	public List<TermComparisonText> findAllByComparisonId(int comparisonId) throws SQLException {
		String sql = "SELECT * FROM term_comparison_texts WHERE comparison_id = ?";
		List<TermComparisonText> termComparisonTexts = new ArrayList<>();

		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, comparisonId);

			try (ResultSet resultSet = statement.executeQuery()) {
				while (resultSet.next()) {
					TermComparisonText termComparisonText = new TermComparisonText(
							resultSet.getInt("comparison_id"),
							resultSet.getInt("text_id"));

					termComparisonTexts.add(termComparisonText);
				}
			}
		}

		return termComparisonTexts;
	}

	public List<TermComparisonText> findAllByTextId(int textId) throws SQLException {
		String sql = "SELECT * FROM term_comparison_texts WHERE text_id = ?";
		List<TermComparisonText> termComparisonTexts = new ArrayList<>();

		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, textId);

			try (ResultSet resultSet = statement.executeQuery()) {
				while (resultSet.next()) {
					TermComparisonText termComparisonText = new TermComparisonText(
							resultSet.getInt("comparison_id"),
							resultSet.getInt("text_id"));

					termComparisonTexts.add(termComparisonText);
				}
			}
		}

		return termComparisonTexts;
	}

	public boolean delete(int comparisonId, int textId) throws SQLException {
		String sql = "DELETE FROM term_comparison_texts WHERE comparison_id = ? AND text_id = ?";

		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, comparisonId);
			statement.setInt(2, textId);

			int deletedRows = statement.executeUpdate();

			return deletedRows > 0;
		}
	}
}
