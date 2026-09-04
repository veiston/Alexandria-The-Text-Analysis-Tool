package com.alexandria.dao;

import com.alexandria.model.TextComparison;
import com.alexandria.model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class TextComparisonDAOTest {
	private UserDAO userDAO;
	private TextComparisonDAO textComparisonDAO;
	private User user;
	private TextComparison textComparison;

	@Before
	public void setup() throws SQLException {
		userDAO = new UserDAO();
		textComparisonDAO = new TextComparisonDAO();

		user = new User(
				"Test user",
				"test-" + UUID.randomUUID() + "@test.com",
				null,
				"Test organization",
				"test-hashed-password"
		);

		user = userDAO.create(user);

		textComparison = new TextComparison(user.getId(), "{\"commonWords\":[]}");
		textComparison = textComparisonDAO.create(textComparison);

		TextComparison secondTextComparison = new TextComparison(user.getId(), "{\"commonWords\":[\"text\"]}");
		textComparisonDAO.create(secondTextComparison);
	}

	@After
	public void cleanup() throws SQLException {
		if (user.getId() != null) {
			userDAO.delete(user.getId());
		}
	}

	@Test
	public void createsTextComparison() {
		assertNotNull(textComparison.getId());
	}

	@Test
	public void findsTextComparisonById() throws SQLException {
		TextComparison foundTextComparison = textComparisonDAO.findById(textComparison.getId());

		assertNotNull(foundTextComparison);
		assertEquals(textComparison.getId(), foundTextComparison.getId());
	}

	@Test
	public void findsTextComparisonsByUserId() throws SQLException {
		List<TextComparison> textComparisons = textComparisonDAO.findAllByUserId(user.getId());

		assertEquals(2, textComparisons.size());
	}

	@Test
	public void updatesTextComparison() throws SQLException {
		textComparison.setComparisonData("{\"commonWords\":[\"updated\"]}");

		assertTrue(textComparisonDAO.update(textComparison));

		TextComparison foundTextComparison = textComparisonDAO.findById(textComparison.getId());
		assertEquals("{\"commonWords\":[\"updated\"]}", foundTextComparison.getComparisonData());
	}

	@Test
	public void deletesTextComparison() throws SQLException {
		assertTrue(textComparisonDAO.delete(textComparison.getId()));
		assertNull(textComparisonDAO.findById(textComparison.getId()));
	}
}
