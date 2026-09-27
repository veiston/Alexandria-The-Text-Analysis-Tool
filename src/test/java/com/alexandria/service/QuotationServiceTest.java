package com.alexandria.service;

import com.alexandria.dao.QuotationDAO;
import com.alexandria.dao.TextDAO;
import com.alexandria.model.FileType;
import com.alexandria.model.Quotation;
import com.alexandria.model.Text;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QuotationServiceTest {

    @Mock
    private QuotationDAO quotationDAO;

    @Mock
    private TextDAO textDAO;

    private QuotationService quotationService;

    @Before
    public void setUp() {
        quotationService = new QuotationService(quotationDAO, textDAO);
    }

    @Test
    public void createsQuotationForTheTextsOwner() throws SQLException {
        Quotation expected = new Quotation(1, 1, "A quotation", "PAGE:1");
        when(textDAO.findById(1)).thenReturn(text(1));
        when(quotationDAO.create(any(Quotation.class))).thenReturn(expected);

        Quotation created = quotationService.create(1, 1, "  A quotation  ", "PAGE:1");

        assertSame(expected, created);
        ArgumentCaptor<Quotation> quotation = ArgumentCaptor.forClass(Quotation.class);
        verify(quotationDAO).create(quotation.capture());
        assertEquals("A quotation", quotation.getValue().getQuotationText());
        assertEquals(Integer.valueOf(1), quotation.getValue().getUserId());
        assertEquals(Integer.valueOf(1), quotation.getValue().getTextId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void doesNotCreateQuotationForAnotherUsersText() throws SQLException {
        when(textDAO.findById(1)).thenReturn(text(1));

        quotationService.create(2, 1, "A quotation", "PAGE:1");
    }

    @Test
    public void findsQuotationsForTheTextsOwner() throws SQLException {
        List<Quotation> expected = List.of(new Quotation(1, 1, "A quotation", "PAGE:1"));
        when(textDAO.findById(1)).thenReturn(text(1));
        when(quotationDAO.findAllByTextId(1)).thenReturn(expected);

        List<Quotation> quotations = quotationService.findAllByTextId(1, 1);

        assertSame(expected, quotations);
        verify(quotationDAO).findAllByTextId(1);
    }

    @Test
    public void updatesOnlyAnOwnedQuotation() throws SQLException {
        Quotation saved = new Quotation(5, 1, 1, "Old text", "PAGE:1", null);
        Quotation edited = new Quotation(5, 99, 99, "  New text  ", "PAGE:2", null);
        when(quotationDAO.findById(5)).thenReturn(saved);
        when(textDAO.findById(1)).thenReturn(text(1));
        when(quotationDAO.update(saved)).thenReturn(true);

        boolean updated = quotationService.update(edited, 1);

        assertEquals(true, updated);
        assertEquals("New text", saved.getQuotationText());
        assertEquals("PAGE:2", saved.getLocation());
        verify(quotationDAO).update(saved);
    }

    @Test(expected = IllegalArgumentException.class)
    public void doesNotDeleteAnotherUsersQuotation() throws SQLException {
        when(quotationDAO.findById(5)).thenReturn(
                new Quotation(5, 2, 1, "A quotation", "PAGE:1", null));

        quotationService.deleteById(5, 1);
    }

    private Text text(int userId) {
        return new Text(1, userId, "Text", "text.txt", FileType.TXT, "Content", null);
    }
}
