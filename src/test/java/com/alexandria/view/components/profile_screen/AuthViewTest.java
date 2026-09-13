package com.alexandria.view.components.profile_screen;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

import javafx.application.Platform;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class AuthViewTest {

    @BeforeClass
    public static void startJavaFx() {
        try {
            Platform.startup(() -> {
            });
        } catch (IllegalStateException ignored) {
        }
    }

    @Test
    public void loginFormIsShownByDefault() {
        AuthView view = new AuthView();
        VBox formHost = (VBox) view.getChildren().get(2);

        assertEquals(view.getLoginForm(), formHost.getChildren().get(0));
    }

    @Test
    public void createAccountShowsRegisterForm() {
        AuthView view = new AuthView();
        HBox toggle = (HBox) view.getChildren().get(1);
        ToggleButton createAccountButton = (ToggleButton) toggle.getChildren().get(1);

        createAccountButton.fire();

        VBox formHost = (VBox) view.getChildren().get(2);
        assertEquals(view.getRegisterForm(), formHost.getChildren().get(0));
    }

    @Test
    public void resetShowsLoginForm() {
        AuthView view = new AuthView();
        HBox toggle = (HBox) view.getChildren().get(1);
        ToggleButton createAccountButton = (ToggleButton) toggle.getChildren().get(1);
        createAccountButton.fire();

        view.reset();

        VBox formHost = (VBox) view.getChildren().get(2);
        assertEquals(view.getLoginForm(), formHost.getChildren().get(0));
    }
}
