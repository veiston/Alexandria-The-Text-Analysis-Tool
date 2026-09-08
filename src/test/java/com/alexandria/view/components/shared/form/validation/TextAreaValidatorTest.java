package com.alexandria.view.components.shared.form.validation;

import com.alexandria.view.components.shared.form.FormField;
import org.junit.Test;

import static org.junit.Assert.*;

public class TextAreaValidatorTest {

    private final TextAreaValidator validator = new TextAreaValidator();
    private final FormField field = new FormField("content", "Content", FormField.Type.TEXT_AREA, true, java.util.List.of());

    @Test
    public void acceptsSmallText() {
        assertNull(validator.validate("A short paragraph.", field));
    }

    @Test
    public void rejectsOverSizeLimit() {
        char[] chars = new char[50 * 1024 * 1024 + 10];
        java.util.Arrays.fill(chars, 'a');
        String oversized = new String(chars);

        assertNotNull(validator.validate(oversized, field));
    }
}