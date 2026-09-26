package com.alexandria.view.components.analyse_screen;

import com.alexandria.model.Quotation;
import com.alexandria.model.QuotationType;
import com.alexandria.view.components.shared.Card;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

public class QuotationCard extends Card {
    private static final double CARD_HEIGHT = 280;
    private static final double QUOTE_AREA_HEIGHT = 180;

    private final Quotation quotation;
    private final Button editButton = iconButton(FontAwesomeSolid.PEN, "Edit");

    public QuotationCard(Quotation quotation) {
        super("");

        this.quotation = quotation;

        setPrefHeight(CARD_HEIGHT);

        QuotationType type = QuotationLocation.parseType(quotation.getLocation());
        setTypeText(type == null ? null : type.label());

        Integer page = QuotationLocation.parsePage(quotation.getLocation());

        if (page != null) {
            addTag("Page " + page).getStyleClass().add("tags-secondary");
        }

        Label quote = new Label("\u201C" + quotation.getQuotationText() + "\u201D");
        quote.getStyleClass().add("quotation-card-preview");
        quote.setWrapText(true);

        ScrollPane quoteScroll = new ScrollPane(quote);
        quoteScroll.getStyleClass().add("shared-scroll");
        quoteScroll.setFitToWidth(true);
        quoteScroll.setPrefHeight(QUOTE_AREA_HEIGHT);
        quoteScroll.setMinHeight(60);
        quoteScroll.setMaxHeight(QUOTE_AREA_HEIGHT);

        setExtraContent(quoteScroll);

        setActionText("Go to \u2192");
        setActionButtonExpand(false);
        setActionAlignment(Pos.CENTER_RIGHT);
        setActionDividerVisible(true);
        getActionButton().getStyleClass().setAll("button", "card-link-action");

        Button copyButton = iconButton(FontAwesomeSolid.COPY, "Copy");
        copyButton.setOnAction(e -> copyQuotationText());

        setSecondaryActions(copyButton, editButton);
    }

    public Quotation getQuotation() {
        return quotation;
    }

    public Button getGoToButton() {
        return getActionButton();
    }

    public Button getEditButton() {
        return editButton;
    }

    private void copyQuotationText() {
        ClipboardContent content = new ClipboardContent();
        content.putString(quotation.getQuotationText());
        Clipboard.getSystemClipboard().setContent(content);
    }

    private static Button iconButton(FontAwesomeSolid icon, String text) {
        FontIcon fontIcon = new FontIcon(icon);
        fontIcon.setIconSize(12);

        Button button = new Button(text, fontIcon);
        button.getStyleClass().setAll("button", "card-pill-button");
        return button;
    }
}