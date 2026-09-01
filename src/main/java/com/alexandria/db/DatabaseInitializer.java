package com.alexandria.db;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {
	public static void initialize() throws IOException, SQLException {
        String schema = Files.readString(Path.of("database/schema.sql"));

		String[] queries = schema.split(";");

		try (Connection connection = DatabaseConnection.getConnection();
            Statement statement = connection.createStatement()) {

            for (String query : queries) {
                statement.execute(query);
            }
        }
    }
}
