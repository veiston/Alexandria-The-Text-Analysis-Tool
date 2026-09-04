package com.alexandria.dao;

import com.alexandria.model.FileType;
import com.alexandria.model.TermComparison;
import com.alexandria.model.TermComparisonText;
import com.alexandria.model.Text;
import com.alexandria.model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TermComparisonTextDAOTest {
	private UserDAO userDAO;
	private TextDAO textDAO;
	private TermComparisonDAO termComparisonDAO;
	private TermComparisonTextDAO termComparisonTextDAO;

	private User user;
	private Text firstText;
	private Text secondText;
	private TermComparison termComparison;
	private TermComparisonText termComparisonText;

	@Before
	public void setup() throws SQLException {
		userDAO = new UserDAO();
		textDAO = new TextDAO();
		termComparisonDAO = new TermComparisonDAO();
		termComparisonTextDAO = new TermComparisonTextDAO();

		user = new User(
				"Test user",
				"test-" + UUID.randomUUID() + "@test.com",
				null,
				"Test organization",
				"test-hashed-password");

		user = userDAO.create(user);

		firstText = new Text(
				user.getId(), "First test text", "first-test.txt", FileType.TXT, "First text content");
		firstText = textDAO.create(firstText);

		secondText = new Text(
				user.getId(), "Second test text", "second-test.txt", FileType.TXT, "Second text content");
		secondText = textDAO.create(secondText);

		termComparison = new TermComparison(user.getId(), "test", "{\"occurrences\":[]}");
		termComparison = termComparisonDAO.create(termComparison);

		TermComparisonText firstTermComparisonText = new TermComparisonText(
				termComparison.getId(), firstText.getId());
		termComparisonTextDAO.create(firstTermComparisonText);

		termComparisonText = new TermComparisonText(
				termComparison.getId(), secondText.getId());
		termComparisonTextDAO.create(termComparisonText);
	}

	@After
	public void cleanup() throws SQLException {
		if (user.getId() != null) {
			userDAO.delete(user.getId());
		}
	}

	@Test
	public void createsTermComparisonText() throws SQLException {
		List<TermComparisonText> termComparisonTexts = termComparisonTextDAO.findAllByComparisonId(
				termComparison.getId());

		assertEquals(2, termComparisonTexts.size());
	}

	@Test
	public void findsTermComparisonTextsByComparisonId() throws SQLException {
		List<TermComparisonText> termComparisonTexts = termComparisonTextDAO.findAllByComparisonId(
				termComparison.getId());

		assertEquals(2, termComparisonTexts.size());
	}

	@Test
	public void findsTermComparisonTextsByTextId() throws SQLException {
		List<TermComparisonText> termComparisonTexts = termComparisonTextDAO.findAllByTextId(firstText.getId());

		assertEquals(1, termComparisonTexts.size());
	}

	@Test
	public void deletesTermComparisonText() throws SQLException {
		assertTrue(termComparisonTextDAO.delete(termComparison.getId(), firstText.getId()));

		List<TermComparisonText> termComparisonTexts = termComparisonTextDAO.findAllByComparisonId(
				termComparison.getId());
		assertEquals(1, termComparisonTexts.size());
	}
}
