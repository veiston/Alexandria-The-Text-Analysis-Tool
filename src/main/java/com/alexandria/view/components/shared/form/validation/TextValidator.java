package com.alexandria.view.components.shared.form.validation;

import com.alexandria.view.components.shared.form.FieldValidator;
import com.alexandria.view.components.shared.form.FormField;

public final class TextValidator implements FieldValidator {

    private static final int MIN_LENGTH = 1;
    private static final int MAX_LENGTH = 50;

    @Override
    public String validate(String value, FormField field) {
        int length = value.trim().length();

        if (length < MIN_LENGTH || length > MAX_LENGTH) {
            return field.label() + " must be between " + MIN_LENGTH + " and " + MAX_LENGTH + " characters.";
        }

        return null;
    }
}