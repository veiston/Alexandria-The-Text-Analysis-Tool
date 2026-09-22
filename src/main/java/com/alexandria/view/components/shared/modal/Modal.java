package com.alexandria.view.components.shared.modal;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

public class Modal extends StackPane {

    private final StackPane contentHost = new StackPane();
    private final Button closeButton = new Button();
    private boolean dismissible = true;

    public Modal() {
        getStyleClass().add("modal-overlay");
        setAlignment(Pos.CENTER);
        setPickOnBounds(true);

        setVisible(false);
        setManaged(false);

        contentHost.setAlignment(Pos.CENTER);
        contentHost.setPickOnBounds(false);
        contentHost.setMaxWidth(Region.USE_PREF_SIZE);
        contentHost.setMaxHeight(Region.USE_PREF_SIZE);

        getChildren().add(contentHost);

        FontIcon closeIcon = new FontIcon(FontAwesomeSolid.TIMES);
        closeIcon.getStyleClass().add("modal-close-icon");

        closeButton.setGraphic(closeIcon);
        closeButton.getStyleClass().add("modal-close-button");
        closeButton.setOnAction(event -> hide());

        StackPane.setAlignment(closeButton, Pos.TOP_RIGHT);
        StackPane.setMargin(closeButton, new Insets(8));

        setOnMouseClicked(event -> {
            if ((event.getTarget() == this || event.getTarget() == contentHost)
                    && dismissible) {
                hide();
            }
        });
    }

    public void setContent(Node content) {
        contentHost.getChildren().setAll(content);

        if (dismissible) {
            contentHost.getChildren().add(closeButton);
        }
    }

    public void show(Node content) {
        show(content, true);
    }

    public void show(Node content, boolean dismissible) {
        this.dismissible = dismissible;
        setContent(content);
        show();
    }

    public void show() {
        setVisible(true);
        setManaged(true);
        toFront();
    }

    public void hide() {
        setVisible(false);
        setManaged(false);
    }

    public boolean isShowing() {
        return isVisible();
    }

    public Node getContent() {
        return contentHost.getChildren().isEmpty()
                ? null
                : contentHost.getChildren().get(0);
    }
}