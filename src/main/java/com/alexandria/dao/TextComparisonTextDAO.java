package com.alexandria.dao;

import com.alexandria.db.DatabaseConnection;
import com.alexandria.model.TextComparisonText;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Text Comparison Texts.
 *
 * Links a text comparison to the texts included in it.
 * Each row contains one comparison ID and one text ID.
 * TODO: Use this DAO in the service to save and load texts for a text comparison.
 */
public class TextComparisonTextDAO {
	public boolean create(TextComparisonText textComparisonText) throws SQLException {
		String sql = "INSERT INTO text_comparison_texts (comparison_id, text_id) VALUES (?, ?)";

		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, textComparisonText.getComparisonId());
			statement.setInt(2, textComparisonText.getTextId());

			int createdRows = statement.executeUpdate();

			return createdRows > 0;
		}
	}

	public List<TextComparisonText> findAllByComparisonId(int comparisonId) throws SQLException {
		String sql = "SELECT * FROM text_comparison_texts WHERE comparison_id = ?";
		List<TextComparisonText> textComparisonTexts = new ArrayList<>();

		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, comparisonId);

			try (ResultSet resultSet = statement.executeQuery()) {
				while (resultSet.next()) {
					TextComparisonText textComparisonText = new TextComparisonText(
							resultSet.getInt("comparison_id"),
							resultSet.getInt("text_id"));

					textComparisonTexts.add(textComparisonText);
				}
			}
		}

		return textComparisonTexts;
	}

	public List<TextComparisonText> findAllByTextId(int textId) throws SQLException {
		String sql = "SELECT * FROM text_comparison_texts WHERE text_id = ?";
		List<TextComparisonText> textComparisonTexts = new ArrayList<>();

		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, textId);

			try (ResultSet resultSet = statement.executeQuery()) {
				while (resultSet.next()) {
					TextComparisonText textComparisonText = new TextComparisonText(
							resultSet.getInt("comparison_id"),
							resultSet.getInt("text_id"));

					textComparisonTexts.add(textComparisonText);
				}
			}
		}

		return textComparisonTexts;
	}

	public boolean delete(int comparisonId, int textId) throws SQLException {
		String sql = "DELETE FROM text_comparison_texts WHERE comparison_id = ? AND text_id = ?";

		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, comparisonId);
			statement.setInt(2, textId);

			int deletedRows = statement.executeUpdate();

			return deletedRows > 0;
		}
	}
}
