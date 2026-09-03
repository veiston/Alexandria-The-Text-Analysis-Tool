package com.alexandria.dao;

import com.alexandria.model.FileType;
import com.alexandria.model.Text;
import com.alexandria.model.TextComparison;
import com.alexandria.model.TextComparisonText;
import com.alexandria.model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TextComparisonTextDAOTest {
	private UserDAO userDAO;
	private TextDAO textDAO;
	private TextComparisonDAO textComparisonDAO;
	private TextComparisonTextDAO textComparisonTextDAO;

	private User user;
	private Text firstText;
	private Text secondText;
	private TextComparison textComparison;
	private TextComparisonText textComparisonText;

	@Before
	public void setup() throws SQLException {
		userDAO = new UserDAO();
		textDAO = new TextDAO();
		textComparisonDAO = new TextComparisonDAO();
		textComparisonTextDAO = new TextComparisonTextDAO();

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

		textComparison = new TextComparison(user.getId(), "{\"commonWords\":[]}");
		textComparison = textComparisonDAO.create(textComparison);

		TextComparisonText firstTextComparisonText = new TextComparisonText(
				textComparison.getId(), firstText.getId());
		textComparisonTextDAO.create(firstTextComparisonText);

		textComparisonText = new TextComparisonText(
				textComparison.getId(), secondText.getId());
		textComparisonTextDAO.create(textComparisonText);
	}

	@After
	public void cleanup() throws SQLException {
		if (user.getId() != null) {
			userDAO.delete(user.getId());
		}
	}

	@Test
	public void createsTextComparisonText() throws SQLException {
		List<TextComparisonText> textComparisonTexts = textComparisonTextDAO.findAllByComparisonId(
				textComparison.getId());

		assertEquals(2, textComparisonTexts.size());
	}

	@Test
	public void findsTextComparisonTextsByComparisonId() throws SQLException {
		List<TextComparisonText> textComparisonTexts = textComparisonTextDAO.findAllByComparisonId(
				textComparison.getId());

		assertEquals(2, textComparisonTexts.size());
	}

	@Test
	public void findsTextComparisonTextsByTextId() throws SQLException {
		List<TextComparisonText> textComparisonTexts = textComparisonTextDAO.findAllByTextId(firstText.getId());

		assertEquals(1, textComparisonTexts.size());
	}

	@Test
	public void deletesTextComparisonText() throws SQLException {
		assertTrue(textComparisonTextDAO.delete(textComparison.getId(), firstText.getId()));

		List<TextComparisonText> textComparisonTexts = textComparisonTextDAO.findAllByComparisonId(
				textComparison.getId());
		assertEquals(1, textComparisonTexts.size());
	}
}
