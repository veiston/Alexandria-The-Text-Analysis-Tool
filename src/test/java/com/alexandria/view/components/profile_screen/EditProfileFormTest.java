package com.alexandria.view.components.profile_screen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import com.alexandria.view.components.shared.form.Form;
import com.alexandria.model.User;

import javafx.application.Platform;

public class EditProfileFormTest {

    @BeforeClass
    public static void startJavaFx() {
        try {
            Platform.startup(() -> {
            });
        } catch (IllegalStateException ignored) {
        }
    }

    @Test
    public void formContainsTextProfileFields() {
        EditProfileForm view = new EditProfileForm();
        Form form = (Form) view.getChildren().get(1);

        assertTrue(form.getValues().containsKey("name"));
        assertTrue(form.getValues().containsKey("email"));
        assertTrue(form.getValues().containsKey("organization"));
    }

    @Test
    public void prefillSetsUserValues() {
        EditProfileForm view = new EditProfileForm();
        Form form = (Form) view.getChildren().get(1);
        User user = new User(
                1,
                "Existing",
                "taken@example.com",
                null,
                "OldOrg",
                "hash");

        view.prefill(user);

        assertEquals("Existing", form.getValues().get("name"));
        assertEquals("taken@example.com", form.getValues().get("email"));
        assertEquals("OldOrg", form.getValues().get("organization"));
    }
}
