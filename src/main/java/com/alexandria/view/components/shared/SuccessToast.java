package com.alexandria.view.components.shared;

import javafx.animation.PauseTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.util.Duration;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

public class SuccessToast extends HBox {

    private final Label message = new Label();
    private final PauseTransition hideTimer = new PauseTransition(Duration.seconds(2.5));

    public SuccessToast() {
        getStyleClass().add("success-toast");
        setAlignment(Pos.CENTER_LEFT);
        setSpacing(8);
        setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        setMouseTransparent(true);
        setVisible(false);

        FontIcon icon = new FontIcon(FontAwesomeSolid.CHECK_CIRCLE);
        icon.getStyleClass().add("success-toast-icon");
        message.getStyleClass().add("success-toast-text");

        getChildren().addAll(icon, message);

        hideTimer.setOnFinished(e -> setVisible(false));
    }

    public void show(String text) {
        message.setText(text);
        setVisible(true);
        hideTimer.playFromStart();
    }
}
