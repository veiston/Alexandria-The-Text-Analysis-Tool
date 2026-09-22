package com.alexandria.view.components.analyse_screen;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

/**
 * TODO(quotations): placeholder until "select text → Mark as
 * quotation" exists. Once it does, add setQuotations(List<...>) and
 * render a PassageCitationCard per marked quotation, allow eddition 
 * (quotation removal from the list). Saved with Save Findings btn
 */
public class QuotationsView extends VBox {

    private final Label placeholderLabel =
            new Label("Current text quotations summary list.");

    public QuotationsView() {
        getStyleClass().add("quotations-view");
        setPadding(new Insets(24));

        placeholderLabel.getStyleClass().add("text-muted");

        ScrollPane scroll = new ScrollPane(placeholderLabel);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("profile-scroll");

        getChildren().add(scroll);
    }
}