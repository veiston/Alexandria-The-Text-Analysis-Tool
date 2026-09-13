package com.alexandria.view.components.profile_screen;

import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import com.alexandria.view.components.shared.form.Form;

import javafx.application.Platform;

public class ChangePasswordFormTest {

    @BeforeClass
    public static void startJavaFx() {
        try {
            Platform.startup(() -> {
            });
        } catch (IllegalStateException ignored) {
        }
    }

    @Test
    public void formContainsPasswordFields() {
        ChangePasswordForm view = new ChangePasswordForm();
        Form form = (Form) view.getChildren().get(1);

        assertTrue(form.getValues().containsKey("newPassword"));
        assertTrue(form.getValues().containsKey("confirmPassword"));
    }
}
