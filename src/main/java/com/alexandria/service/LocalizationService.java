package com.alexandria.service;

import java.text.MessageFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import javafx.geometry.NodeOrientation;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;

/*Localization Service.
Loads bundles from src/main/resources/i18n and provides locale-aware translations,
dynamic scene-graph localization, and number/date formatting for the UI.*/
public class LocalizationService {

    public static final Locale ENGLISH = Locale.US;
    public static final Locale JAPANESE = Locale.JAPAN;
    /* Russian doesn't seem to have a short version available in Locale. */
    public static final Locale RUSSIAN = new Locale("ru", "RU");
    public static final Locale ARABIC = new Locale("ar");

    private static final String BUNDLE_BASE = "i18n.MessagesBundle";
    private static final ResourceBundle.Control NO_FALLBACK =
            ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_PROPERTIES);

    private static final LocalizationService INSTANCE = new LocalizationService();

    private final List<Consumer<Locale>> listeners = new ArrayList<>();

    private Locale locale;
    private ResourceBundle bundle;
    private NumberFormat numberFormat;
    private DateTimeFormatter dateFormatter;

    public LocalizationService() {
        setLocale(ENGLISH);
    }

    public static LocalizationService getInstance() {
        return INSTANCE;
    }

    public void addListener(Consumer<Locale> listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    public void setLocale(Locale locale) {
        ResourceBundle newBundle = ResourceBundle.getBundle(BUNDLE_BASE, locale, NO_FALLBACK);
        NumberFormat newNumberFormat = NumberFormat.getNumberInstance(locale);
        DateTimeFormatter newDateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale);

        this.bundle = newBundle;
        this.numberFormat = newNumberFormat;
        this.dateFormatter = newDateFormatter;
        this.locale = locale;

        for (Consumer<Locale> listener : listeners) {
            listener.accept(locale);
        }
    }

    public boolean isRtl() {
        return locale != null && "ar".equals(locale.getLanguage());
    }

    public String get(String key) {
        return bundle.getString(key);
    }

    public String get(String key, Object... args) {
        return MessageFormat.format(bundle.getString(key), args);
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

    public Locale getLocale() {
        return locale;
    }

    /**
     * Applies layout orientation (RTL/LTR) across the node hierarchy without text manipulation.
     */
    public void apply(Node node) {
        if (node == null) {
            return;
        }

        if (!node.nodeOrientationProperty().isBound()) {
            node.setNodeOrientation(isRtl() ? NodeOrientation.RIGHT_TO_LEFT : NodeOrientation.LEFT_TO_RIGHT);
        }

        if (node instanceof ScrollPane scrollPane) {
            apply(scrollPane.getContent());
        } else if (node instanceof Parent parent) {
            for (Node child : parent.getChildrenUnmodifiable()) {
                apply(child);
            }
        }
    }
}
