package com.alexandria.view.components.shared.form;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public final class FormValidator {

    private final Function<Map<String, String>, String> customValidator;

    public FormValidator(Function<Map<String, String>, String> customValidator) {
        this.customValidator = customValidator;
    }

    public String validate(List<FormField> fields, FormRenderer renderer) {
        for (FormField field : fields) {
            String error = validateField(field, renderer);
            if (error != null) {
                return error;
            }
        }

        if (customValidator != null) {
            String error = customValidator.apply(renderer.getValues());
            if (error != null) {
                return error;
            }
        }

        return null;
    }

    private String validateField(FormField field, FormRenderer renderer) {
        if (renderer.isEmpty(field)) {
            return field.required() ? field.label() + " is required." : null;
        }

        String value = renderer.getValue(field);

        for (FieldValidator validator : field.validators()) {
            String error = validator.validate(value, field);
            if (error != null) {
                return error;
            }
        }

        return null;
    }
}
