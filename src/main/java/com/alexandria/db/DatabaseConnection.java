package com.alexandria.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
	private static final String URL = "jdbc:mariadb://localhost:3306/alexandria";
	private static final String USER = "alexandria";
	private static final String PASSWORD = "alexandria";

	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(URL, USER, PASSWORD);
	}
}
