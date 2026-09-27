package com.alexandria.service;

import com.alexandria.dao.QuotationDAO;
import com.alexandria.dao.TextDAO;
import com.alexandria.model.Quotation;
import com.alexandria.model.Text;

import java.sql.SQLException;
import java.util.List;

public class QuotationService {
    private final QuotationDAO quotationDAO;
    private final TextDAO textDAO;

    public QuotationService() {
        this(new QuotationDAO(), new TextDAO());
    }

    QuotationService(QuotationDAO quotationDAO, TextDAO textDAO) {
        this.quotationDAO = quotationDAO;
        this.textDAO = textDAO;
    }

    public Quotation create(int userId, int textId, String quotationText, String location) throws SQLException {
        validateQuotationText(quotationText);
        requireOwnedText(textId, userId);

        Quotation quotation = new Quotation(userId, textId, quotationText.strip(), location);
        return quotationDAO.create(quotation);
    }

    public List<Quotation> findAllByTextId(int textId, int userId) throws SQLException {
        requireOwnedText(textId, userId);
        return quotationDAO.findAllByTextId(textId);
    }

    public boolean update(Quotation quotation, int userId) throws SQLException {
        if (quotation == null || quotation.getId() == null) {
            throw new IllegalArgumentException("A saved quotation is required.");
        }

        validateQuotationText(quotation.getQuotationText());

        Quotation savedQuotation = requireOwnedQuotation(quotation.getId(), userId);
        savedQuotation.setQuotationText(quotation.getQuotationText().strip());
        savedQuotation.setLocation(quotation.getLocation());

        return quotationDAO.update(savedQuotation);
    }

    public boolean deleteById(int quotationId, int userId) throws SQLException {
        requireOwnedQuotation(quotationId, userId);
        return quotationDAO.delete(quotationId);
    }

    private Text requireOwnedText(int textId, int userId) throws SQLException {
        Text text = textDAO.findById(textId);

        if (text == null) {
            throw new IllegalArgumentException("Text with the provided ID was not found.");
        }

        if (!Integer.valueOf(userId).equals(text.getUserId())) {
            throw new IllegalArgumentException("Text does not belong to the provided user.");
        }

        return text;
    }

    private Quotation requireOwnedQuotation(int quotationId, int userId) throws SQLException {
        Quotation quotation = quotationDAO.findById(quotationId);

        if (quotation == null) {
            throw new IllegalArgumentException("Quotation with the provided ID was not found.");
        }

        if (!Integer.valueOf(userId).equals(quotation.getUserId())) {
            throw new IllegalArgumentException("Quotation does not belong to the provided user.");
        }

        requireOwnedText(quotation.getTextId(), userId);
        return quotation;
    }

    private void validateQuotationText(String quotationText) {
        if (quotationText == null || quotationText.isBlank()) {
            throw new IllegalArgumentException("Quotation text cannot be empty.");
        }
    }
}
