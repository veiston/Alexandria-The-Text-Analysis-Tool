package com.alexandria.model;

import com.alexandria.service.LocalizationService;

/**
 * The kind of note a user attaches to a document selection.
 */
public enum QuotationType {
    DIRECT("quotations.directCitation"),
    INDIRECT("quotations.indirectCitation"),
    ANNOTATION("quotations.annotation");

    private final String localizationKey;

    QuotationType(String localizationKey) {
        this.localizationKey = localizationKey;
    }

    public String getLocalizationKey() {
        return localizationKey;
    }

    public String getLocalizedLabel() {
        return LocalizationService.getInstance().get(localizationKey);
    }
}
