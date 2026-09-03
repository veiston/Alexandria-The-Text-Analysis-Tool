package com.alexandria.dao;

import com.alexandria.db.DatabaseConnection;
import com.alexandria.model.FileType;
import com.alexandria.model.Text;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TextDAO {
	public Text create(Text text) throws SQLException {
		String sql = "INSERT INTO texts (user_id, title, file_name, file_type, content) VALUES (?, ?, ?, ?, ?)";

		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			statement.setInt(1, text.getUserId());
			statement.setString(2, text.getTitle());
			statement.setString(3, text.getFileName());
			statement.setString(4, text.getFileType().name());
			statement.setString(5, text.getContent());

			statement.executeUpdate();

			try (ResultSet keys = statement.getGeneratedKeys()) {
				if (keys.next()) {
					text.setId(keys.getInt(1));
				}
			}
		}

		return text;
	}

	public Text findById(int id) throws SQLException {
		String sql = "SELECT * FROM texts WHERE id = ?";

		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, id);

			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					String fileType = resultSet.getString("file_type");

					return new Text(
							resultSet.getInt("id"),
							resultSet.getInt("user_id"),
							resultSet.getString("title"),
							resultSet.getString("file_name"),
							FileType.valueOf(fileType),
							resultSet.getString("content"),
							resultSet.getTimestamp("created_at").toLocalDateTime());
				}
			}
		}

		return null;
	}

	public List<Text> findAllByUserId(int userId) throws SQLException {
		String sql = "SELECT * FROM texts WHERE user_id = ?";

		List<Text> texts = new ArrayList<>();

		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, userId);

			try (ResultSet resultSet = statement.executeQuery()) {
				while (resultSet.next()) {
					String fileType = resultSet.getString("file_type");

					Text text = new Text(
							resultSet.getInt("id"),
							resultSet.getInt("user_id"),
							resultSet.getString("title"),
							resultSet.getString("file_name"),
							FileType.valueOf(fileType),
							resultSet.getString("content"),
							resultSet.getTimestamp("created_at").toLocalDateTime());

					texts.add(text);
				}
			}
		}

		return texts;
	}

	public boolean update(Text text) throws SQLException {
		String sql = "UPDATE texts SET title = ?, file_name = ?, file_type = ?, content = ? WHERE id = ?";

		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, text.getTitle());
			statement.setString(2, text.getFileName());

			statement.setString(3, text.getFileType().name());
			statement.setString(4, text.getContent());
			statement.setInt(5, text.getId());

			int updatedRows = statement.executeUpdate();

			return updatedRows > 0;
		}
	}

	public boolean delete(int id) throws SQLException {
		String sql = "DELETE FROM texts WHERE id = ?";

		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, id);

			int deletedRows = statement.executeUpdate();

			return deletedRows > 0;
		}
	}
}
