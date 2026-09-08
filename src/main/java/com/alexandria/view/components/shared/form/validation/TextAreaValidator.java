package com.alexandria.view.components.shared.form.validation;

import java.nio.charset.StandardCharsets;

import com.alexandria.view.components.shared.form.FieldValidator;
import com.alexandria.view.components.shared.form.FormField;

public final class TextAreaValidator implements FieldValidator {

    private static final long MAX_SIZE = 50L * 1024 * 1024;

    @Override
    public String validate(String value, FormField field) {
        long size = value.getBytes(StandardCharsets.UTF_8).length;

        if (size > MAX_SIZE) {
            return field.label() + " must not exceed 50 MiB.";
        }

        return null;
    }
}