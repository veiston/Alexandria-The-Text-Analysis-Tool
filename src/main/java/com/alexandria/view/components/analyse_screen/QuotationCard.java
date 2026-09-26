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
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

import java.util.function.Consumer;

public class QuotationCard extends Card {
    private static final double CARD_HEIGHT = 280;
    private static final double QUOTE_AREA_HEIGHT = 180;

    private final Quotation quotation;
    private final Button editButton = iconButton(FontAwesomeSolid.PEN, "Edit");
    private final TextArea quoteEditor = new TextArea();

    private boolean editing = false;
    private Consumer<Quotation> onEdit = quotation -> {
    };

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

        quoteEditor.setText(quotation.getQuotationText());
        quoteEditor.setWrapText(true);
        quoteEditor.setPrefHeight(QUOTE_AREA_HEIGHT);
        quoteEditor.setMinHeight(60);
        quoteEditor.setMaxHeight(QUOTE_AREA_HEIGHT);
        quoteEditor.getStyleClass().add("quotation-card-editor");
        quoteEditor.setVisible(false);
        quoteEditor.setManaged(false);

        setExtraContent(quoteScroll);

        setActionText("Go to \u2192");
        setActionButtonExpand(false);
        setActionAlignment(Pos.CENTER_RIGHT);
        setActionDividerVisible(true);
        getActionButton().getStyleClass().setAll("button", "card-link-action");

        Button copyButton = iconButton(FontAwesomeSolid.COPY, "Copy");
        copyButton.setOnAction(e -> copyQuotationText());

        editButton.setOnAction(e -> toggleEdit(quote, quoteScroll));

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

    public void setOnEdit(Consumer<Quotation> handler) {
        onEdit = handler == null ? quotation -> {
        } : handler;
    }

    private void toggleEdit(Label quote, ScrollPane quoteScroll) {
        if (!editing) {
            editing = true;

            quoteEditor.setText(quotation.getQuotationText());

            quoteScroll.setVisible(false);
            quoteScroll.setManaged(false);

            setExtraContent(quoteEditor);

            quoteEditor.setVisible(true);
            quoteEditor.setManaged(true);

            editButton.setText("Update");

            FontIcon icon = new FontIcon(FontAwesomeSolid.SAVE);
            icon.setIconSize(12);
            editButton.setGraphic(icon);

        } else {
            String updatedText = quoteEditor.getText();

            if (updatedText != null && !updatedText.isBlank()) {
                updatedText = updatedText.strip();

                quotation.setQuotationText(updatedText);
                quote.setText("\u201C" + updatedText + "\u201D");

                onEdit.accept(quotation);
            }

            editing = false;

            quoteEditor.setVisible(false);
            quoteEditor.setManaged(false);

            setExtraContent(quoteScroll);

            quoteScroll.setVisible(true);
            quoteScroll.setManaged(true);

            editButton.setText("Edit");

            FontIcon icon = new FontIcon(FontAwesomeSolid.PEN);
            icon.setIconSize(12);
            editButton.setGraphic(icon);
        }
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
