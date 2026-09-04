package com.alexandria.dao;

import com.alexandria.model.FileType;
import com.alexandria.model.Text;
import com.alexandria.model.TextAnalysis;
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

public class TextAnalysisDAOTest {
	private UserDAO userDAO;
	private TextDAO textDAO;
	private TextAnalysisDAO textAnalysisDAO;
	
	private User user;
	private Text text;
	private TextAnalysis textAnalysis;

	@Before
	public void setup() throws SQLException {
		userDAO = new UserDAO();
		textDAO = new TextDAO();
		textAnalysisDAO = new TextAnalysisDAO();

		user = new User(
				"Test user",
				"test-" + UUID.randomUUID() + "@test.com",
				null,
				"Test organization",
				"test-hashed-password"
		);

		user = userDAO.create(user);

		text = new Text(
				user.getId(),
				"Test text",
				"test.txt",
				FileType.TXT,
				"Test text content"
		);

		text = textDAO.create(text);

		textAnalysis = new TextAnalysis(user.getId(), text.getId(), "{\"totalWords\":3}");
		textAnalysis = textAnalysisDAO.create(textAnalysis);

		TextAnalysis secondTextAnalysis = new TextAnalysis(user.getId(), text.getId(), "{\"totalWords\":5}");
		textAnalysisDAO.create(secondTextAnalysis);
	}

	@After
	public void cleanup() throws SQLException {
		if (user.getId() != null) {
			userDAO.delete(user.getId());
		}
	}

	@Test
	public void createsTextAnalysis() {
		assertNotNull(textAnalysis.getId());
	}

	@Test
	public void findsTextAnalysisById() throws SQLException {
		TextAnalysis foundTextAnalysis = textAnalysisDAO.findById(textAnalysis.getId());

		assertNotNull(foundTextAnalysis);
		assertEquals(textAnalysis.getId(), foundTextAnalysis.getId());
	}

	@Test
	public void findsTextAnalysesByUserId() throws SQLException {
		List<TextAnalysis> textAnalyses = textAnalysisDAO.findAllByUserId(user.getId());

		assertEquals(2, textAnalyses.size());
	}

	@Test
	public void findsTextAnalysesByTextId() throws SQLException {
		List<TextAnalysis> textAnalyses = textAnalysisDAO.findAllByTextId(text.getId());

		assertEquals(2, textAnalyses.size());
	}

	@Test
	public void updatesTextAnalysis() throws SQLException {
		textAnalysis.setAnalysisData("{\"totalWords\":10}");

		assertTrue(textAnalysisDAO.update(textAnalysis));

		TextAnalysis foundTextAnalysis = textAnalysisDAO.findById(textAnalysis.getId());
		assertEquals("{\"totalWords\":10}", foundTextAnalysis.getAnalysisData());
	}

	@Test
	public void deletesTextAnalysis() throws SQLException {
		assertTrue(textAnalysisDAO.delete(textAnalysis.getId()));
		assertNull(textAnalysisDAO.findById(textAnalysis.getId()));
	}
}
