package com.alexandria.view.components.profile_screen;

import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import com.alexandria.view.components.shared.form.Form;

import javafx.application.Platform;

public class RegisterFormTest {

    @BeforeClass
    public static void startJavaFx() {
        try {
            Platform.startup(() -> {
            });
        } catch (IllegalStateException ignored) {
        }
    }

    @Test
    public void formContainsTextRegistrationFields() {
        RegisterForm view = new RegisterForm();
        Form form = (Form) view.getChildren().get(0);

        assertTrue(form.getValues().containsKey("name"));
        assertTrue(form.getValues().containsKey("email"));
        assertTrue(form.getValues().containsKey("password"));
        assertTrue(form.getValues().containsKey("organization"));
    }
}
