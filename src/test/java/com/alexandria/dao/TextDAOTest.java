package com.alexandria.dao;

import com.alexandria.model.FileType;
import com.alexandria.model.Text;
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

public class TextDAOTest {
	private UserDAO userDAO;
	private TextDAO textDAO;
	private User user;
	private Text text;

	@Before
	public void setup() throws SQLException {
		user = new User(
				"Test user",
				"test-" + UUID.randomUUID() + "@test.com",
				null,
				"Test organization",
				"test-hashed-password"
		);

		userDAO = new UserDAO();
		user = userDAO.create(user);

		
		text = new Text(
				user.getId(),
				"Test text",
				"test.txt",
				FileType.TXT,
				"Test text content"
		);

		textDAO = new TextDAO();
		text = textDAO.create(text);

		Text secondText = new Text(
				user.getId(),
				"Second test text",
				"second-test.txt",
				FileType.TXT,
				"Second test text content"
		);

		textDAO.create(secondText);
	}

	@After
	public void cleanup() throws SQLException {
		if (user.getId() != null) {
			userDAO.delete(user.getId());
		}
	}

	@Test
	public void createsText() {
		assertNotNull(text.getId());
	}

	@Test
	public void findsTextById() throws SQLException {
		Text foundText = textDAO.findById(text.getId());

		assertNotNull(foundText);
		assertEquals(text.getId(), foundText.getId());
	}

	@Test
	public void findsTextsByUserId() throws SQLException {
		List<Text> texts = textDAO.findAllByUserId(user.getId());

		assertEquals(2, texts.size());
	}

	@Test
	public void updatesText() throws SQLException {
		text.setTitle("Updated test text");
		text.setFileName("updated-test.pdf");
		text.setFileType(FileType.PDF);
		text.setContent("Updated test text content");

		assertTrue(textDAO.update(text));

		Text foundText = textDAO.findById(text.getId());
		assertEquals("Updated test text", foundText.getTitle());
		assertEquals("updated-test.pdf", foundText.getFileName());
		assertEquals(FileType.PDF, foundText.getFileType());
		assertEquals("Updated test text content", foundText.getContent());
	}

	@Test
	public void deletesText() throws SQLException {
		assertTrue(textDAO.delete(text.getId()));
		assertNull(textDAO.findById(text.getId()));
	}
}
