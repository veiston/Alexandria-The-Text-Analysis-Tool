package com.alexandria.controller;

import com.alexandria.model.Quotation;
import com.alexandria.service.QuotationService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class QuotationController {

    private Integer currentUserId;
    private Integer currentTextId;
    private final List<Quotation> quotations = new ArrayList<>();

    private final QuotationService quotationService;

    public QuotationController() {
        this(new QuotationService());
    }

    QuotationController(QuotationService quotationService) {
        this.quotationService = quotationService;
    }

    public void openText(Integer userId, Integer textId) throws SQLException {
        this.currentUserId = userId;
        this.currentTextId = textId;

        reloadQuotations();
    }

    public Quotation addQuotation(String quotationText, String location) throws SQLException {
        if (!canSaveQuotations() || quotationText == null || quotationText.isBlank()) {
            return null;
        }
        String trimmed = quotationText.strip();

        for (Quotation existing : quotations) {
            if (trimmed.equals(existing.getQuotationText())
                    && Objects.equals(location, existing.getLocation())) {
                return existing;
            }
        }

        Quotation quotation = quotationService.create(currentUserId, currentTextId, quotationText, location);

        quotations.add(quotation);

        return quotation;
    }

    public boolean removeQuotation(int quotationId) throws SQLException {
        if (!canSaveQuotations() || !quotationService.deleteById(quotationId, currentUserId)) {
            return false;
        }

        return quotations.removeIf(quotation -> Objects.equals(quotation.getId(), quotationId));
    }

    public boolean updateQuotation(Quotation quotation) throws SQLException {
        if (!canSaveQuotations()) {
            return false;
        }

        return quotationService.update(quotation, currentUserId);
    }

    private boolean canSaveQuotations() {
        return currentUserId != null && currentTextId != null;
    }

    public void reloadQuotations() throws SQLException {
        quotations.clear();

        if (canSaveQuotations()) {
            quotations.addAll(quotationService.findAllByTextId(currentTextId, currentUserId));
        }
    }

    public List<Quotation> getQuotations() {
        return List.copyOf(quotations);
    }
}