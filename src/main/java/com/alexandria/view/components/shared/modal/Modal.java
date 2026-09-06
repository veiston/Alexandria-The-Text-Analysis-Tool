package com.alexandria.view.components.shared.modal;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

public class Modal extends StackPane {

    private final StackPane contentHost = new StackPane();
    private boolean dismissible = true;

    public Modal() {
        getStyleClass().add("modal-overlay");
        setAlignment(Pos.CENTER);
        setPickOnBounds(true);

        setVisible(false);
        setManaged(false);

        contentHost.setAlignment(Pos.CENTER);
        contentHost.setPickOnBounds(false);
        getChildren().add(contentHost);

        setOnMouseClicked(event -> {
            if ((event.getTarget() == this || event.getTarget() == contentHost) && dismissible) {
                hide();
            }
        });
    }

    public void setContent(Node content) {
        contentHost.getChildren().setAll(content);
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