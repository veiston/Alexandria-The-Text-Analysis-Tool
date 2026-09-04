package com.alexandria.dao;

import com.alexandria.db.DatabaseConnection;
import com.alexandria.model.TermComparison;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Text Comparison with entered word/phrase.
 *
 * Manages a comparison of a word or phrase entered by the user across several texts,
 * including its total occurrences.
 */
public class TermComparisonDAO {
	public TermComparison create(TermComparison termComparison) throws SQLException {
		String sql = "INSERT INTO term_comparisons (user_id, term, comparison_data) VALUES (?, ?, ?)";

		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			statement.setInt(1, termComparison.getUserId());
			statement.setString(2, termComparison.getTerm());
			statement.setString(3, termComparison.getComparisonData());

			statement.executeUpdate();

			try (ResultSet keys = statement.getGeneratedKeys()) {
				if (keys.next()) {
					termComparison.setId(keys.getInt(1));
				}
			}
		}

		return termComparison;
	}

	public TermComparison findById(int id) throws SQLException {
		String sql = "SELECT * FROM term_comparisons WHERE id = ?";

		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, id);

			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					return new TermComparison(
							resultSet.getInt("id"),
							resultSet.getInt("user_id"),
							resultSet.getString("term"),
							resultSet.getString("comparison_data"),
							resultSet.getTimestamp("created_at").toLocalDateTime());
				}
			}
		}

		return null;
	}

	public List<TermComparison> findAllByUserId(int userId) throws SQLException {
		String sql = "SELECT * FROM term_comparisons WHERE user_id = ?";
		List<TermComparison> termComparisons = new ArrayList<>();

		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, userId);

			try (ResultSet resultSet = statement.executeQuery()) {
				while (resultSet.next()) {
					TermComparison termComparison = new TermComparison(
							resultSet.getInt("id"),
							resultSet.getInt("user_id"),
							resultSet.getString("term"),
							resultSet.getString("comparison_data"),
							resultSet.getTimestamp("created_at").toLocalDateTime());

					termComparisons.add(termComparison);
				}
			}
		}

		return termComparisons;
	}

	public boolean update(TermComparison termComparison) throws SQLException {
		String sql = "UPDATE term_comparisons SET term = ?, comparison_data = ? WHERE id = ?";

		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, termComparison.getTerm());
			statement.setString(2, termComparison.getComparisonData());
			statement.setInt(3, termComparison.getId());

			int updatedRows = statement.executeUpdate();

			return updatedRows > 0;
		}
	}

	public boolean delete(int id) throws SQLException {
		String sql = "DELETE FROM term_comparisons WHERE id = ?";

		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, id);

			int deletedRows = statement.executeUpdate();

			return deletedRows > 0;
		}
	}
}
