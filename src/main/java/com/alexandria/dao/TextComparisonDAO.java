package com.alexandria.dao;

import com.alexandria.db.DatabaseConnection;
import com.alexandria.model.TextComparison;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Text Comparison.
 *
 * Manages a comparison of two or more texts, including common frequently used words.
 */
public class TextComparisonDAO {
	public TextComparison create(TextComparison comparison) throws SQLException {
		String sql = "INSERT INTO text_comparisons (user_id, comparison_data) VALUES (?, ?)";

		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			statement.setInt(1, comparison.getUserId());
			statement.setString(2, comparison.getComparisonData());
			
			statement.executeUpdate();

			try (ResultSet keys = statement.getGeneratedKeys()) {
				if (keys.next()) {
					comparison.setId(keys.getInt(1));
				}
			}
		}

		return comparison;
	}

	public TextComparison findById(int id) throws SQLException {
		String sql = "SELECT * FROM text_comparisons WHERE id = ?";

		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, id);

			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					return new TextComparison(
							resultSet.getInt("id"),
							resultSet.getInt("user_id"),
							resultSet.getString("comparison_data"),
							resultSet.getTimestamp("created_at").toLocalDateTime());
				}
			}
		}

		return null;
	}

	public List<TextComparison> findAllByUserId(int userId) throws SQLException {
		String sql = "SELECT * FROM text_comparisons WHERE user_id = ?";
		List<TextComparison> comparisons = new ArrayList<>();

		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, userId);

			try (ResultSet resultSet = statement.executeQuery()) {
				while (resultSet.next()) {
					TextComparison comparison = new TextComparison(
							resultSet.getInt("id"),
							resultSet.getInt("user_id"),
							resultSet.getString("comparison_data"),
							resultSet.getTimestamp("created_at").toLocalDateTime());

					comparisons.add(comparison);
				}
			}
		}

		return comparisons;
	}

	public boolean update(TextComparison comparison) throws SQLException {
		String sql = "UPDATE text_comparisons SET comparison_data = ? WHERE id = ?";

		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, comparison.getComparisonData());
			statement.setInt(2, comparison.getId());

			int updatedRows = statement.executeUpdate();

			return updatedRows > 0;
		}
	}

	public boolean delete(int id) throws SQLException {
		String sql = "DELETE FROM text_comparisons WHERE id = ?";

		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, id);

			int deletedRows = statement.executeUpdate();

			return deletedRows > 0;
		}
	}
}
