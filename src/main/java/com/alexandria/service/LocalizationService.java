package com.alexandria.service;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.ResourceBundle;

/*  * Localization Service
Loads bundles from src/main/resources/i18n and provides locale-aware translations,
number formatting, and date formatting for the UI. */

public class LocalizationService {

    private static final String BUNDLE_BASE = "i18n.MessagesBundle";
    private static final ResourceBundle.Control NO_FALLBACK =
            ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_PROPERTIES);

    public static final Locale ENGLISH = Locale.US;
    public static final Locale JAPANESE = Locale.JAPAN;
    /* Russian doesn't seem to have a short version available in Locale. */
    public static final Locale RUSSIAN = new Locale("ru", "RU");

    private Locale locale;
    private ResourceBundle bundle;
    private NumberFormat numberFormat;
    private DateTimeFormatter dateFormatter;

    public LocalizationService() {
        setLocale(ENGLISH);
    }

    public void setLocale(Locale locale) {
        ResourceBundle newBundle = ResourceBundle.getBundle(BUNDLE_BASE, locale, NO_FALLBACK);
        NumberFormat newNumberFormat = NumberFormat.getNumberInstance(locale);
        DateTimeFormatter newDateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale);

        this.bundle = newBundle;
        this.numberFormat = newNumberFormat;
        this.dateFormatter = newDateFormatter;
        this.locale = locale;
    }

    public Locale getLocale() {
        return locale;
    }

    public String get(String key) {
        return bundle.getString(key);
    }

    public String formatNumber(long number) {
        return numberFormat.format(number);
    }

    public String formatNumber(double number) {
        return numberFormat.format(number);
    }

    public String formatDate(LocalDate date) {
        return date.format(dateFormatter);
    }
}
