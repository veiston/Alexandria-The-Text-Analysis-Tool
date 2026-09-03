package com.alexandria.dao;

import com.alexandria.db.DatabaseConnection;
import com.alexandria.model.SearchResult;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Text Search.
 *
 * Manages saved results of a search for a word or phrase entered by the user.
 * Search settings are selected by the user.
 */
public class SearchResultDAO {
	public SearchResult create(SearchResult searchResult) throws SQLException {
		String sql = "INSERT INTO search_results (user_id, text_id, query, results_data) VALUES (?, ?, ?, ?)";

		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			statement.setInt(1, searchResult.getUserId());
			statement.setInt(2, searchResult.getTextId());
			statement.setString(3, searchResult.getQuery());
			statement.setString(4, searchResult.getResultsData());

			statement.executeUpdate();

			try (ResultSet keys = statement.getGeneratedKeys()) {
				if (keys.next()) {
					searchResult.setId(keys.getInt(1));
				}
			}
		}

		return searchResult;
	}

	public SearchResult findById(int id) throws SQLException {
		String sql = "SELECT * FROM search_results WHERE id = ?";

		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, id);

			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					return new SearchResult(
							resultSet.getInt("id"),
							resultSet.getInt("user_id"),
							resultSet.getInt("text_id"),
							resultSet.getString("query"),
							resultSet.getString("results_data"),
							resultSet.getTimestamp("created_at").toLocalDateTime());
				}
			}
		}

		return null;
	}

	public List<SearchResult> findAllByUserId(int userId) throws SQLException {
		String sql = "SELECT * FROM search_results WHERE user_id = ?";
		List<SearchResult> searchResults = new ArrayList<>();

		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, userId);

			try (ResultSet resultSet = statement.executeQuery()) {
				while (resultSet.next()) {
					SearchResult searchResult = new SearchResult(
							resultSet.getInt("id"),
							resultSet.getInt("user_id"),
							resultSet.getInt("text_id"),
							resultSet.getString("query"),
							resultSet.getString("results_data"),
							resultSet.getTimestamp("created_at").toLocalDateTime());

					searchResults.add(searchResult);
				}
			}
		}

		return searchResults;
	}

	public List<SearchResult> findAllByTextId(int textId) throws SQLException {
		String sql = "SELECT * FROM search_results WHERE text_id = ?";
		List<SearchResult> searchResults = new ArrayList<>();

		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, textId);

			try (ResultSet resultSet = statement.executeQuery()) {
				while (resultSet.next()) {
					SearchResult searchResult = new SearchResult(
							resultSet.getInt("id"),
							resultSet.getInt("user_id"),
							resultSet.getInt("text_id"),
							resultSet.getString("query"),
							resultSet.getString("results_data"),
							resultSet.getTimestamp("created_at").toLocalDateTime());

					searchResults.add(searchResult);
				}
			}
		}

		return searchResults;
	}

	public boolean update(SearchResult searchResult) throws SQLException {
		String sql = "UPDATE search_results SET query = ?, results_data = ? WHERE id = ?";

		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, searchResult.getQuery());
			statement.setString(2, searchResult.getResultsData());
			statement.setInt(3, searchResult.getId());

			int updatedRows = statement.executeUpdate();

			return updatedRows > 0;
		}
	}

	public boolean delete(int id) throws SQLException {
		String sql = "DELETE FROM search_results WHERE id = ?";

		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, id);

			int deletedRows = statement.executeUpdate();

			return deletedRows > 0;
		}
	}
}
