package com.alexandria.view.components.shared.form.validation;

import com.alexandria.view.components.shared.form.FormField;
import org.junit.Test;

import static org.junit.Assert.*;

public class EmailValidatorTest {

    private final EmailValidator validator = new EmailValidator();
    private final FormField field = new FormField("email", "Email", FormField.Type.TEXT, true, java.util.List.of());

    @Test
    public void acceptsValidEmail() {
        assertNull(validator.validate("user@example.com", field));
    }

    @Test
    public void rejectsMissingAtSign() {
        assertNotNull(validator.validate("userexample.com", field));
    }

    @Test
    public void rejectsMissingDomain() {
        assertNotNull(validator.validate("user@", field));
    }

    @Test
    public void rejectsOverMaxLength() {
        String longEmail = "a".repeat(45) + "@a.com"; // over 50 chars total
        assertNotNull(validator.validate(longEmail, field));
    }

    @Test
    public void trimsWhitespaceBeforeChecking() {
        assertNull(validator.validate("  user@example.com  ", field));
    }
}