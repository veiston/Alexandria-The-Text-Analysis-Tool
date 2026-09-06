package com.alexandria.view.components.shared.form;

public class FormField {
    public enum Type {
        TEXT, EMAIL, PASSWORD, FILE
    }

    private final String key;
    private final String label;
    private final Type type;
    private final boolean required;

    public FormField(String key, String label, Type type, boolean required) {
        this.key = key;
        this.label = label;
        this.type = type;
        this.required = required;
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
}