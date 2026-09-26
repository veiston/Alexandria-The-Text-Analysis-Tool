package com.alexandria.model;

/**
 * The kind of note a user attaches to a document selection.
 */
public enum QuotationType {
    DIRECT("Direct citation"),
    INDIRECT("Indirect citation"),
    ANNOTATION("Annotation");

    private final String label;

    QuotationType(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }
}