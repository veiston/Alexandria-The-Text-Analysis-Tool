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
	private TextAnalysisDAO analysisDAO;
	
	private User user;
	private Text text;
	private TextAnalysis analysis;

	@Before
	public void setup() throws SQLException {
		userDAO = new UserDAO();
		textDAO = new TextDAO();
		analysisDAO = new TextAnalysisDAO();

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

		analysis = new TextAnalysis(user.getId(), text.getId(), "{\"totalWords\":3}");
		analysis = analysisDAO.create(analysis);

		TextAnalysis secondAnalysis = new TextAnalysis(user.getId(), text.getId(), "{\"totalWords\":5}");
		analysisDAO.create(secondAnalysis);
	}

	@After
	public void cleanup() throws SQLException {
		if (user.getId() != null) {
			userDAO.delete(user.getId());
		}
	}

	@Test
	public void createsTextAnalysis() {
		assertNotNull(analysis.getId());
	}

	@Test
	public void findsTextAnalysisById() throws SQLException {
		TextAnalysis foundAnalysis = analysisDAO.findById(analysis.getId());

		assertNotNull(foundAnalysis);
		assertEquals(analysis.getId(), foundAnalysis.getId());
	}

	@Test
	public void findsTextAnalysesByUserId() throws SQLException {
		List<TextAnalysis> analyses = analysisDAO.findAllByUserId(user.getId());

		assertEquals(2, analyses.size());
	}

	@Test
	public void findsTextAnalysesByTextId() throws SQLException {
		List<TextAnalysis> analyses = analysisDAO.findAllByTextId(text.getId());

		assertEquals(2, analyses.size());
	}

	@Test
	public void updatesTextAnalysis() throws SQLException {
		analysis.setAnalysisData("{\"totalWords\":10}");

		assertTrue(analysisDAO.update(analysis));

		TextAnalysis foundAnalysis = analysisDAO.findById(analysis.getId());
		assertEquals("{\"totalWords\":10}", foundAnalysis.getAnalysisData());
	}

	@Test
	public void deletesTextAnalysis() throws SQLException {
		assertTrue(analysisDAO.delete(analysis.getId()));
		assertNull(analysisDAO.findById(analysis.getId()));
	}
}
