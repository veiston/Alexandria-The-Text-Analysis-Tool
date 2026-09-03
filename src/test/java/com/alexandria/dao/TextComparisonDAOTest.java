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
	private TextComparisonDAO comparisonDAO;
	private User user;
	private TextComparison comparison;

	@Before
	public void setup() throws SQLException {
		userDAO = new UserDAO();
		comparisonDAO = new TextComparisonDAO();

		user = new User(
				"Test user",
				"test-" + UUID.randomUUID() + "@test.com",
				null,
				"Test organization",
				"test-hashed-password"
		);

		user = userDAO.create(user);

		comparison = new TextComparison(user.getId(), "{\"commonWords\":[]}");
		comparison = comparisonDAO.create(comparison);

		TextComparison secondComparison = new TextComparison(user.getId(), "{\"commonWords\":[\"text\"]}");
		comparisonDAO.create(secondComparison);
	}

	@After
	public void cleanup() throws SQLException {
		if (user.getId() != null) {
			userDAO.delete(user.getId());
		}
	}

	@Test
	public void createsTextComparison() {
		assertNotNull(comparison.getId());
	}

	@Test
	public void findsTextComparisonById() throws SQLException {
		TextComparison foundComparison = comparisonDAO.findById(comparison.getId());

		assertNotNull(foundComparison);
		assertEquals(comparison.getId(), foundComparison.getId());
	}

	@Test
	public void findsTextComparisonsByUserId() throws SQLException {
		List<TextComparison> comparisons = comparisonDAO.findAllByUserId(user.getId());

		assertEquals(2, comparisons.size());
	}

	@Test
	public void updatesTextComparison() throws SQLException {
		comparison.setComparisonData("{\"commonWords\":[\"updated\"]}");

		assertTrue(comparisonDAO.update(comparison));

		TextComparison foundComparison = comparisonDAO.findById(comparison.getId());
		assertEquals("{\"commonWords\":[\"updated\"]}", foundComparison.getComparisonData());
	}

	@Test
	public void deletesTextComparison() throws SQLException {
		assertTrue(comparisonDAO.delete(comparison.getId()));
		assertNull(comparisonDAO.findById(comparison.getId()));
	}
}
