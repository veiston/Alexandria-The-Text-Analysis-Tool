package com.alexandria.controller;

import com.alexandria.model.Quotation;
import com.alexandria.view.components.analyse_screen.QuotationLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class QuotationController {

    private Integer currentUserId;
    private Integer currentTextId;
    private int nextId = 1;

    private final List<Quotation> quotations = new ArrayList<>();

    public void openText(Integer userId, Integer textId) {
        this.currentUserId = userId;
        this.currentTextId = textId;

        quotations.clear();
        nextId = 1;
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

        quotation.setId(nextId++);

        quotations.add(quotation);

        // TODO: Persist via QuotationDAO.create(quotation) once the
        // quotations screen/service has a real signed-in user id and
        // persisted text id to attach it to.
        // Currently quotations are calculated/kept in memory only.
        // IMPORTANT: do not remove in-memory storage, it is used for
        // guests without an account.

        System.out.println(
                "[Quotation added] id=" + quotation.getId()
                        + ", type=" + QuotationLocation.parseType(location)
                        + ", userId=" + currentUserId
                        + ", textId=" + currentTextId
                        + ", location=" + location
                        + ", text=\"" + quotation.getQuotationText() + "\"");

        return quotation;
    }

    public boolean removeQuotation(int quotationId) {
        boolean removed = quotations.removeIf(
                quotation -> Objects.equals(
                        quotation.getId(),
                        quotationId));

        if (removed) {
            // TODO: Persist removal via QuotationDAO.delete(quotationId)
            // once persistence exists.
            System.out.println(
                    "[Quotation removed] id=" + quotationId);
        }

        return removed;
    }

    public List<Quotation> getQuotations() {
        return List.copyOf(quotations);
    }
}