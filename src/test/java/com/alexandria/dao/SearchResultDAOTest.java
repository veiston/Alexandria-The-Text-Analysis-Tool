package com.alexandria.dao;

import com.alexandria.model.FileType;
import com.alexandria.model.SearchResult;
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

public class SearchResultDAOTest {
	private UserDAO userDAO;
	private TextDAO textDAO;
	private SearchResultDAO searchResultDAO;

	private User user;
	private Text text;
	private SearchResult searchResult;

	@Before
	public void setup() throws SQLException {
		userDAO = new UserDAO();
		textDAO = new TextDAO();
		searchResultDAO = new SearchResultDAO();

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

		searchResult = new SearchResult(user.getId(), text.getId(), "test", "{\"matches\":[]}");
		searchResult = searchResultDAO.create(searchResult);

		SearchResult secondSearchResult = new SearchResult(
				user.getId(), text.getId(), "text", "{\"matches\":[]}");
		searchResultDAO.create(secondSearchResult);
	}

	@After
	public void cleanup() throws SQLException {
		if (user.getId() != null) {
			userDAO.delete(user.getId());
		}
	}

	@Test
	public void createsSearchResult() {
		assertNotNull(searchResult.getId());
	}

	@Test
	public void findsSearchResultById() throws SQLException {
		SearchResult foundSearchResult = searchResultDAO.findById(searchResult.getId());

		assertNotNull(foundSearchResult);
		assertEquals(searchResult.getId(), foundSearchResult.getId());
	}

	@Test
	public void findsSearchResultsByUserId() throws SQLException {
		List<SearchResult> searchResults = searchResultDAO.findAllByUserId(user.getId());

		assertEquals(2, searchResults.size());
	}

	@Test
	public void findsSearchResultsByTextId() throws SQLException {
		List<SearchResult> searchResults = searchResultDAO.findAllByTextId(text.getId());

		assertEquals(2, searchResults.size());
	}

	@Test
	public void updatesSearchResult() throws SQLException {
		searchResult.setQuery("updated");
		searchResult.setResultsData("{\"matches\":[\"updated\"]}");

		assertTrue(searchResultDAO.update(searchResult));

		SearchResult foundSearchResult = searchResultDAO.findById(searchResult.getId());
		assertEquals("updated", foundSearchResult.getQuery());
		assertEquals("{\"matches\":[\"updated\"]}", foundSearchResult.getResultsData());
	}

	@Test
	public void deletesSearchResult() throws SQLException {
		assertTrue(searchResultDAO.delete(searchResult.getId()));
		assertNull(searchResultDAO.findById(searchResult.getId()));
	}
}
