package com.alexandria.view.components.shared;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class Card extends VBox {
    private final VBox details = new VBox(4);
    private final Button deleteButton = createDeleteButton();
    private final Label type = new Label();
    private final Label source = new Label();
    private final Label footerText = new Label();
    private final Button actionButton = new Button();
    private Node extraContent;

    public Card(String titleText) {

        getStyleClass().add("card");
        setSpacing(12);
        setPrefWidth(280);
        setPrefHeight(200);

        type.getStyleClass().add("tags");
        type.setVisible(false);
        type.setManaged(false);

        AnchorPane header = new AnchorPane(type, deleteButton);
        header.setPrefHeight(20);
        AnchorPane.setTopAnchor(type, 0.0);
        AnchorPane.setLeftAnchor(type, 0.0);
        AnchorPane.setTopAnchor(deleteButton, -10.0);
        AnchorPane.setRightAnchor(deleteButton, 0.0);

        Label title = new Label(titleText);
        title.getStyleClass().add("heading-md");
        title.setWrapText(true);

        source.getStyleClass().add("text-muted");
        source.setWrapText(true);
        source.setVisible(false);
        source.setManaged(false);

        details.getChildren().addAll(title, source);

        footerText.getStyleClass().addAll("text-muted", "mono-text");
        footerText.setVisible(false);
        footerText.setManaged(false);
        Region footerSpacer = new Region();
        HBox.setHgrow(footerSpacer, Priority.ALWAYS);
        HBox footer = new HBox(footerSpacer, footerText);
        footer.setAlignment(Pos.CENTER_LEFT);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        actionButton.getStyleClass().addAll("button", "primary");
        actionButton.setMaxWidth(Double.MAX_VALUE);
        actionButton.setVisible(false);
        actionButton.setManaged(false);

        getChildren().addAll(header, details, spacer, footer, actionButton);
    }

    public void setExtraContent(Node content) {
        if (extraContent != null) {
            details.getChildren().remove(extraContent);
        }

        extraContent = content;

        if (extraContent != null) {
            details.getChildren().add(1, extraContent);
        }
    }

    public void setTypeText(String text) {
        type.setText(text);
        type.setVisible(text != null && !text.isBlank());
        type.setManaged(type.isVisible());
    }

    public void setSourceText(String text) {
        source.setText(text);
        source.setVisible(text != null && !text.isBlank());
        source.setManaged(source.isVisible());
    }

    public void setFooterText(String text) {
        footerText.setText(text);
        footerText.setVisible(text != null && !text.isBlank());
        footerText.setManaged(footerText.isVisible());
    }

    public void setActionText(String text) {
        actionButton.setText(text);
        actionButton.setVisible(text != null && !text.isBlank());
        actionButton.setManaged(actionButton.isVisible());
    }

    public Button getDeleteButton() {
        return deleteButton;
    }

    public Button getActionButton() {
        return actionButton;
    }

    private Button createDeleteButton() {
        FontIcon icon = new FontIcon(FontAwesomeSolid.TRASH_ALT);
        icon.setIconSize(12);

        Button delete = new Button();
        delete.setGraphic(icon);
        delete.setTooltip(new Tooltip("Delete"));
        delete.getStyleClass().addAll("button", "card-delete-button");
        return delete;
    }
}
