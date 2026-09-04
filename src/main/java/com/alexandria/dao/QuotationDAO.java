package com.alexandria.dao;

import com.alexandria.db.DatabaseConnection;
import com.alexandria.model.Quotation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class QuotationDAO {
	public Quotation create(Quotation quotation) throws SQLException {
		String sql = "INSERT INTO quotations (user_id, text_id, quotation_text, location) VALUES (?, ?, ?, ?)";

		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			statement.setInt(1, quotation.getUserId());
			statement.setInt(2, quotation.getTextId());
			statement.setString(3, quotation.getQuotationText());
			statement.setString(4, quotation.getLocation());

			statement.executeUpdate();

			try (ResultSet keys = statement.getGeneratedKeys()) {
				if (keys.next()) {
					quotation.setId(keys.getInt(1));
				}
			}
		}

		return quotation;
	}

	public Quotation findById(int id) throws SQLException {
		String sql = "SELECT * FROM quotations WHERE id = ?";

		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, id);

			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					return new Quotation(
							resultSet.getInt("id"),
							resultSet.getInt("user_id"),
							resultSet.getInt("text_id"),
							resultSet.getString("quotation_text"),
							resultSet.getString("location"),
							resultSet.getTimestamp("created_at").toLocalDateTime());
				}
			}
		}

		return null;
	}

	public List<Quotation> findAllByUserId(int userId) throws SQLException {
		String sql = "SELECT * FROM quotations WHERE user_id = ?";
		List<Quotation> quotations = new ArrayList<>();

		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, userId);

			try (ResultSet resultSet = statement.executeQuery()) {
				while (resultSet.next()) {
					Quotation quotation = new Quotation(
							resultSet.getInt("id"),
							resultSet.getInt("user_id"),
							resultSet.getInt("text_id"),
							resultSet.getString("quotation_text"),
							resultSet.getString("location"),
							resultSet.getTimestamp("created_at").toLocalDateTime());

					quotations.add(quotation);
				}
			}
		}

		return quotations;
	}

	public List<Quotation> findAllByTextId(int textId) throws SQLException {
		String sql = "SELECT * FROM quotations WHERE text_id = ?";
		List<Quotation> quotations = new ArrayList<>();

		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, textId);

			try (ResultSet resultSet = statement.executeQuery()) {
				while (resultSet.next()) {
					Quotation quotation = new Quotation(
							resultSet.getInt("id"),
							resultSet.getInt("user_id"),
							resultSet.getInt("text_id"),
							resultSet.getString("quotation_text"),
							resultSet.getString("location"),
							resultSet.getTimestamp("created_at").toLocalDateTime());

					quotations.add(quotation);
				}
		}
		}

		return quotations;
	}

	public boolean update(Quotation quotation) throws SQLException {
		String sql = "UPDATE quotations SET quotation_text = ?, location = ? WHERE id = ?";

		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, quotation.getQuotationText());
			statement.setString(2, quotation.getLocation());
			statement.setInt(3, quotation.getId());

			int updatedRows = statement.executeUpdate();

			return updatedRows > 0;
		}
	}

	public boolean delete(int id) throws SQLException {
		String sql = "DELETE FROM quotations WHERE id = ?";

		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, id);

			int deletedRows = statement.executeUpdate();

			return deletedRows > 0;
		}
	}
}
