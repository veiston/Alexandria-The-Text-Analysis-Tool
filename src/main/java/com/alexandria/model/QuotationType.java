package com.alexandria.model;

import com.alexandria.service.LocalizationService;

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

    public String getLocalizedLabel() {
        LocalizationService i18n = LocalizationService.getInstance();
        return switch (this) {
            case DIRECT -> i18n.get("quotations.directCitation");
            case INDIRECT -> i18n.get("quotations.indirectCitation");
            case ANNOTATION -> i18n.get("quotations.annotation");
        };
    }
}
