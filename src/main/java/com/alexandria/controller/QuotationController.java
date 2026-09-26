package com.alexandria.controller;

import com.alexandria.model.Quotation;

import java.util.ArrayList;
import java.util.List;

public class QuotationController {
    private Integer currentUserId;
    private Integer currentTextId;

    private final List<Quotation> quotations = new ArrayList<>();

    public void openText(Integer userId, Integer textId) {
        this.currentUserId = userId;
        this.currentTextId = textId;

        quotations.clear();
    }

    public Quotation addQuotation(String quotationText, String location) {
        if (quotationText == null || quotationText.isBlank()) {
            return null;
        }

        Quotation quotation = new Quotation(
                currentUserId,
                currentTextId,
                quotationText.strip(),
                location);

        quotations.add(quotation);

        // TODO: Persist the quotation via QuotationDAO.create(quotation)
        // once the quotations screen/service exists and we have a real
        // signed-in user id + persisted text id to attach it to.
        // Currently quotations are calculated/kept in memory only.
        // IMPORTANT: do not remove in-memory storage, it is used for guests
        // without an account.

        System.out.println(
                "[Quotation added] userId=" + currentUserId
                        + ", textId=" + currentTextId
                        + ", location=" + location
                        + ", text=\"" + quotation.getQuotationText() + "\"");

        return quotation;
    }

    public List<Quotation> getQuotations() {
        return List.copyOf(quotations);
    }
}