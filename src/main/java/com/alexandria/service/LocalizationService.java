package com.alexandria.service;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import javafx.geometry.NodeOrientation;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Labeled;
import javafx.scene.control.TextInputControl;
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
    private static final String I18N_KEY_PROPERTY = "i18n.key";
    private static final ResourceBundle.Control NO_FALLBACK =
            ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_PROPERTIES);

    private static final LocalizationService INSTANCE = new LocalizationService();

    private final Map<String, String> defaultTextToKey = new HashMap<>();
    private final List<Consumer<Locale>> listeners = new ArrayList<>();

    private Locale locale;
    private ResourceBundle bundle;
    private NumberFormat numberFormat;
    private DateTimeFormatter dateFormatter;

    public LocalizationService() {
        populateDefaultTextIndex();
        setLocale(ENGLISH);
    }

    public static LocalizationService getInstance() {
        return INSTANCE;
    }

    private void populateDefaultTextIndex() {
        ResourceBundle defaultBundle = ResourceBundle.getBundle(BUNDLE_BASE, ENGLISH, NO_FALLBACK);
        for (String key : defaultBundle.keySet()) {
            defaultTextToKey.putIfAbsent(defaultBundle.getString(key), key);
        }
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

    /*Update UI text*/
    public void apply(Node node) {
        if (node == null) {
            return;
        }

        if (!node.nodeOrientationProperty().isBound()) {
            node.setNodeOrientation(isRtl() ? NodeOrientation.RIGHT_TO_LEFT : NodeOrientation.LEFT_TO_RIGHT);
        }

        if (node instanceof Labeled labeled) {
            updateLabeledText(labeled);
        } else if (node instanceof TextInputControl textInput) {
            updateTextInputPrompt(textInput);
        }

        if (node instanceof Parent parent) {
            for (Node child : parent.getChildrenUnmodifiable()) {
                apply(child);
            }
        }
    }

    private void updateLabeledText(Labeled labeled) {
        String key = (String) labeled.getProperties().get(I18N_KEY_PROPERTY);
        if (key == null) {
            key = defaultTextToKey.get(labeled.getText());
            if (key != null) {
                labeled.getProperties().put(I18N_KEY_PROPERTY, key);
            }
        }
        if (key != null && bundle.containsKey(key)) {
            if (!labeled.textProperty().isBound()) {
                labeled.setText(bundle.getString(key));
            }
        }
    }

    private void updateTextInputPrompt(TextInputControl textInput) {
        String key = (String) textInput.getProperties().get(I18N_KEY_PROPERTY);
        if (key == null) {
            key = defaultTextToKey.get(textInput.getPromptText());
            if (key != null) {
                textInput.getProperties().put(I18N_KEY_PROPERTY, key);
            }
        }
        if (key != null && bundle.containsKey(key)) {
            if (!textInput.promptTextProperty().isBound()) {
                textInput.setPromptText(bundle.getString(key));
            }
        }
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
