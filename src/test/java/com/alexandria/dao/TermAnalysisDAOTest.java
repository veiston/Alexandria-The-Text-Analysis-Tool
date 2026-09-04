package com.alexandria.dao;

import com.alexandria.model.FileType;
import com.alexandria.model.TermAnalysis;
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

public class TermAnalysisDAOTest {
	private UserDAO userDAO;
	private TextDAO textDAO;
	private TermAnalysisDAO termAnalysisDAO;

	private User user;
	private Text text;
	private TermAnalysis termAnalysis;

	@Before
	public void setup() throws SQLException {
		userDAO = new UserDAO();
		textDAO = new TextDAO();
		termAnalysisDAO = new TermAnalysisDAO();

		user = new User(
				"Test user",
				"test-" + UUID.randomUUID() + "@test.com",
				null,
				"Test organization",
				"test-hashed-password");

		user = userDAO.create(user);

		text = new Text(
				user.getId(),
				"Test text",
				"test.txt",
				FileType.TXT,
				"Test text content");

		text = textDAO.create(text);

		termAnalysis = new TermAnalysis(user.getId(), text.getId(), "test", "{\"occurrences\":1}");
		termAnalysis = termAnalysisDAO.create(termAnalysis);

		TermAnalysis secondTermAnalysis = new TermAnalysis(
				user.getId(), text.getId(), "text", "{\"occurrences\":1}");
		termAnalysisDAO.create(secondTermAnalysis);
	}

	@After
	public void cleanup() throws SQLException {
		if (user.getId() != null) {
			userDAO.delete(user.getId());
		}
	}

	@Test
	public void createsTermAnalysis() {
		assertNotNull(termAnalysis.getId());
	}

	@Test
	public void findsTermAnalysisById() throws SQLException {
		TermAnalysis foundTermAnalysis = termAnalysisDAO.findById(termAnalysis.getId());

		assertNotNull(foundTermAnalysis);
		assertEquals(termAnalysis.getId(), foundTermAnalysis.getId());
	}

	@Test
	public void findsTermAnalysesByUserId() throws SQLException {
		List<TermAnalysis> termAnalyses = termAnalysisDAO.findAllByUserId(user.getId());

		assertEquals(2, termAnalyses.size());
	}

	@Test
	public void findsTermAnalysesByTextId() throws SQLException {
		List<TermAnalysis> termAnalyses = termAnalysisDAO.findAllByTextId(text.getId());

		assertEquals(2, termAnalyses.size());
	}

	@Test
	public void updatesTermAnalysis() throws SQLException {
		termAnalysis.setTerm("updated");
		termAnalysis.setAnalysisData("{\"occurrences\":2}");

		assertTrue(termAnalysisDAO.update(termAnalysis));

		TermAnalysis foundTermAnalysis = termAnalysisDAO.findById(termAnalysis.getId());
		assertEquals("updated", foundTermAnalysis.getTerm());
		assertEquals("{\"occurrences\":2}", foundTermAnalysis.getAnalysisData());
	}

	@Test
	public void deletesTermAnalysis() throws SQLException {
		assertTrue(termAnalysisDAO.delete(termAnalysis.getId()));
		assertNull(termAnalysisDAO.findById(termAnalysis.getId()));
	}
}
