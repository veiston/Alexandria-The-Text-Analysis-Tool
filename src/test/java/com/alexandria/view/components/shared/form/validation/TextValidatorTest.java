package com.alexandria.view.components.shared.form.validation;

import com.alexandria.view.components.shared.form.FormField;
import org.junit.Test;

import static org.junit.Assert.*;

public class TextValidatorTest {

    private final TextValidator validator = new TextValidator();
    private final FormField field = new FormField("name", "Name", FormField.Type.TEXT, true, java.util.List.of());

    @Test
    public void acceptsValueWithinRange() {
        assertNull(validator.validate("Lua", field));
    }

    @Test
    public void rejectsWhitespaceOnlyValue() {
        assertNotNull(validator.validate("   ", field));
    }

    @Test
    public void acceptsExactlyMaxLength() {
        String value = "a".repeat(50);
        assertNull(validator.validate(value, field));
    }

    @Test
    public void rejectsOverMaxLength() {
        String value = "a".repeat(51);
        assertNotNull(validator.validate(value, field));
    }
}