package com.alexandria.dao;

import com.alexandria.db.DatabaseConnection;
import com.alexandria.model.Language;
import com.alexandria.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UserDAO {

	public User create(User user) throws SQLException {
		String sql = "INSERT INTO users (name, email, photo, organization, password, locale) VALUES (?, ?, ?, ?, ?, ?)";

		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			statement.setString(1, user.getName());
			statement.setString(2, user.getEmail());
			statement.setString(3, user.getPhoto());
			statement.setString(4, user.getOrganization());
			statement.setString(5, user.getPassword());
			statement.setString(6, user.getLocale().name());

			statement.executeUpdate();

			try (ResultSet keys = statement.getGeneratedKeys()) {
				if (keys.next()) {
					user.setId(keys.getInt(1));
				}
			}
		}

		return user;
	}

	public User findById(int id) throws SQLException {
		String sql = "SELECT * FROM users WHERE id = ?";

		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, id);

			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					return new User(
							resultSet.getInt("id"),
							resultSet.getString("name"),
							resultSet.getString("email"),
							resultSet.getString("photo"),
							resultSet.getString("organization"),
							resultSet.getString("password"),
							Language.valueOf(resultSet.getString("locale")));
				}
			}

		}

		return null;
	}

	public User findByEmail(String email) throws SQLException {
		String sql = "SELECT * FROM users WHERE email = ?";

		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, email);
			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					return new User(
							resultSet.getInt("id"),
							resultSet.getString("name"),
							resultSet.getString("email"),
							resultSet.getString("photo"),
							resultSet.getString("organization"),
							resultSet.getString("password"),
							Language.valueOf(resultSet.getString("locale")));
				}
			}

		}

		return null;
	}

	public boolean update(User user) throws SQLException {
		String sql = "UPDATE users SET name = ?, email = ?, photo = ?, organization = ?, password = ?, locale = ? WHERE id = ?";

		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, user.getName());
			statement.setString(2, user.getEmail());
			statement.setString(3, user.getPhoto());
			statement.setString(4, user.getOrganization());
			statement.setString(5, user.getPassword());
			statement.setString(6, user.getLocale().name());
			statement.setInt(7, user.getId());

			int updatedRows = statement.executeUpdate();

			return updatedRows > 0;
		}
	}

	public boolean updateLocale(int userId, Language locale) throws SQLException {
		String sql = "UPDATE users SET locale = ? WHERE id = ?";
		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {

			statement.setString(1, locale.name());
			statement.setInt(2, userId);
			
			return statement.executeUpdate() > 0;
		}
	}

	public boolean delete(int id) throws SQLException {
		String sql = "DELETE FROM users WHERE id = ?";

		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {

			statement.setInt(1, id);

			int deletedRows = statement.executeUpdate();

			return deletedRows > 0;
		}
	}
}
