package com.alexandria.view.components.shared.form.validation;

import com.alexandria.view.components.shared.form.FieldValidator;
import com.alexandria.view.components.shared.form.FormField;

public final class PasswordValidator implements FieldValidator {

    private static final int MIN_LENGTH = 8;

    @Override
    public String validate(String value, FormField field) {
        if (value.length() < MIN_LENGTH) {
            return field.label() + " must be at least " + MIN_LENGTH + " characters.";
        }

        return null;
    }
}

