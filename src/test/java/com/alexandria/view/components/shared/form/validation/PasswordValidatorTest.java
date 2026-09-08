package com.alexandria.view.components.shared.form.validation;

import com.alexandria.view.components.shared.form.FormField;
import org.junit.Test;

import static org.junit.Assert.*;

public class PasswordValidatorTest {

    private final PasswordValidator validator = new PasswordValidator();
    private final FormField field = new FormField("password", "Password", FormField.Type.PASSWORD, true, java.util.List.of());

    @Test
    public void rejectsTooShort() {
        assertNotNull(validator.validate("short7", field));
    }

    @Test
    public void acceptsExactlyMinLength() {
        assertNull(validator.validate("exactly8", field));
    }

    @Test
    public void acceptsLongPassword() {
        assertNull(validator.validate("a-fairly-long-password", field));
    }
}