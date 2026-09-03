package com.alexandria.dao;

import com.alexandria.model.TermComparison;
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

public class TermComparisonDAOTest {
	private UserDAO userDAO;
	private TermComparisonDAO termComparisonDAO;
	private User user;
	private TermComparison termComparison;

	@Before
	public void setup() throws SQLException {
		userDAO = new UserDAO();
		termComparisonDAO = new TermComparisonDAO();

		user = new User(
				"Test user",
				"test-" + UUID.randomUUID() + "@test.com",
				null,
				"Test organization",
				"test-hashed-password"
		);

		user = userDAO.create(user);

		termComparison = new TermComparison(user.getId(), "test", "{\"occurrences\":[]}");
		termComparison = termComparisonDAO.create(termComparison);

		TermComparison secondTermComparison = new TermComparison(
				user.getId(), "text", "{\"occurrences\":[1]}"
		);
		termComparisonDAO.create(secondTermComparison);
	}

	@After
	public void cleanup() throws SQLException {
		if (user.getId() != null) {
			userDAO.delete(user.getId());
		}
	}

	@Test
	public void createsTermComparison() {
		assertNotNull(termComparison.getId());
	}

	@Test
	public void findsTermComparisonById() throws SQLException {
		TermComparison foundTermComparison = termComparisonDAO.findById(termComparison.getId());

		assertNotNull(foundTermComparison);
		assertEquals(termComparison.getId(), foundTermComparison.getId());
	}

	@Test
	public void findsTermComparisonsByUserId() throws SQLException {
		List<TermComparison> termComparisons = termComparisonDAO.findAllByUserId(user.getId());

		assertEquals(2, termComparisons.size());
	}

	@Test
	public void updatesTermComparison() throws SQLException {
		termComparison.setTerm("updated");
		termComparison.setComparisonData("{\"occurrences\":[2]}");

		assertTrue(termComparisonDAO.update(termComparison));

		TermComparison foundTermComparison = termComparisonDAO.findById(termComparison.getId());
		assertEquals("updated", foundTermComparison.getTerm());
		assertEquals("{\"occurrences\":[2]}", foundTermComparison.getComparisonData());
	}

	@Test
	public void deletesTermComparison() throws SQLException {
		assertTrue(termComparisonDAO.delete(termComparison.getId()));
		assertNull(termComparisonDAO.findById(termComparison.getId()));
	}
}
