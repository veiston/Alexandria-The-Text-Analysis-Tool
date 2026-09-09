package com.alexandria.view.components.shared.form;

import java.util.List;

public final class FormField {

    public enum Type {
        TEXT,
        TEXT_AREA,
        PASSWORD,
        IMG_FILE,
        DOCUMENT_FILE
    }

    private final String key;
    private final String label;
    private final Type type;
    private final boolean required;
    private final List<FieldValidator> validators;

    public FormField(
            String key,
            String label,
            Type type,
            boolean required,
            List<FieldValidator> validators) {

        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("Field key must not be blank.");
        }

        if (label == null || label.isBlank()) {
            throw new IllegalArgumentException("Field label must not be blank.");
        }

        if (type == null) {
            throw new IllegalArgumentException("Field type must not be null.");
        }

        this.key = key;
        this.label = label;
        this.type = type;
        this.required = required;
        this.validators = List.copyOf(validators);
    }

    public String key() {
        return key;
    }

    public String label() {
        return label;
    }

    public Type type() {
        return type;
    }

    public boolean required() {
        return required;
    }

    public List<FieldValidator> validators() {
        return validators;
    }
}
