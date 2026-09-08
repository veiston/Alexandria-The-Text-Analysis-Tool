package com.alexandria.view.components.shared.form.validation;

import java.util.regex.Pattern;

import com.alexandria.view.components.shared.form.FieldValidator;
import com.alexandria.view.components.shared.form.FormField;

public final class EmailValidator implements FieldValidator {

    private static final int MAX_LENGTH = 50;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$");

    @Override
    public String validate(String value, FormField field) {
        String email = value.trim();

        if (email.length() > MAX_LENGTH || !EMAIL_PATTERN.matcher(email).matches()) {
            return "Please enter a valid email address.";
        }

        return null;
    }
}



