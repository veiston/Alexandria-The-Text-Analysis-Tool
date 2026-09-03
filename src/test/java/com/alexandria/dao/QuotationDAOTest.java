package com.alexandria.dao;

import com.alexandria.model.FileType;
import com.alexandria.model.Quotation;
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

public class QuotationDAOTest {
	private UserDAO userDAO;
	private TextDAO textDAO;
	private QuotationDAO quotationDAO;

	private User user;
	private Text text;
	private Quotation quotation;

	@Before
	public void setup() throws SQLException {
		userDAO = new UserDAO();
		textDAO = new TextDAO();
		quotationDAO = new QuotationDAO();

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

		quotation = new Quotation(user.getId(), text.getId(), "Test quotation", "Page 1");
		quotation = quotationDAO.create(quotation);

		Quotation secondQuotation = new Quotation(
				user.getId(), text.getId(), "Second quotation", "Page 2");
		quotationDAO.create(secondQuotation);
	}

	@After
	public void cleanup() throws SQLException {
		if (user.getId() != null) {
			userDAO.delete(user.getId());
		}
	}

	@Test
	public void createsQuotation() {
		assertNotNull(quotation.getId());
	}

	@Test
	public void findsQuotationById() throws SQLException {
		Quotation foundQuotation = quotationDAO.findById(quotation.getId());

		assertNotNull(foundQuotation);
		assertEquals(quotation.getId(), foundQuotation.getId());
	}

	@Test
	public void findsQuotationsByUserId() throws SQLException {
		List<Quotation> quotations = quotationDAO.findAllByUserId(user.getId());

		assertEquals(2, quotations.size());
	}

	@Test
	public void findsQuotationsByTextId() throws SQLException {
		List<Quotation> quotations = quotationDAO.findAllByTextId(text.getId());

		assertEquals(2, quotations.size());
	}

	@Test
	public void updatesQuotation() throws SQLException {
		quotation.setQuotationText("Updated quotation");
		quotation.setLocation("Page 3");

		assertTrue(quotationDAO.update(quotation));

		Quotation foundQuotation = quotationDAO.findById(quotation.getId());
		assertEquals("Updated quotation", foundQuotation.getQuotationText());
		assertEquals("Page 3", foundQuotation.getLocation());
	}

	@Test
	public void deletesQuotation() throws SQLException {
		assertTrue(quotationDAO.delete(quotation.getId()));
		assertNull(quotationDAO.findById(quotation.getId()));
	}
}
