package com.alexandria.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
	private static final String URL = getProperty("db.url", "jdbc:mariadb://localhost:3306/alexandria");
	private static final String USER = getProperty("db.user", "alexandria");
	private static final String PASSWORD = getProperty("db.password", "alexandria");

	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(URL, USER, PASSWORD);
	}

	private static String getProperty(String name, String defaultValue) {
		return System.getProperty(name, defaultValue);
	}
}
