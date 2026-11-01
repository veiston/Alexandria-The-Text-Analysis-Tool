package com.alexandria.view.screens;

import java.util.function.Consumer;

import com.alexandria.model.Language;
import com.alexandria.service.LocalizationService;
import com.alexandria.utils.ThemeSettings;
import com.alexandria.utils.UserGuideSettings;
import com.alexandria.view.components.shared.ToggleSwitch;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class SettingsScreen extends VBox {

    private final Label title = new Label();
    private final Label subtitle = new Label();
    private final Label appearanceTitle = new Label();
    private final Label darkThemeLabel = new Label();
    private final Label languageTitle = new Label();
    private final Label languageDescription = new Label();
    private final Label userGuideTitle = new Label();
    private final Label userGuideLabel = new Label();
    private final ComboBox<Language> languageSelector = new ComboBox<>(
            FXCollections.observableArrayList(Language.values()));
    private Consumer<Language> onLanguageChanged = language -> {
    };
    private boolean updatingLanguage;

    public SettingsScreen() {
        setPadding(new Insets(30));
        setSpacing(16);
        setMaxWidth(Double.MAX_VALUE);

        title.getStyleClass().add("heading-xl");

        subtitle.getStyleClass().add("text-muted");

        appearanceTitle.getStyleClass().add("heading-lg");

        darkThemeLabel.getStyleClass().add("heading-md");
        HBox.setHgrow(darkThemeLabel, Priority.ALWAYS);

        ToggleSwitch darkThemeToggle = new ToggleSwitch();

        darkThemeToggle.setSelected(ThemeSettings.isDarkTheme());
        darkThemeToggle.setOnAction(event -> ThemeSettings.setDarkTheme(
                getScene() == null ? null : getScene().getRoot(),
                darkThemeToggle.isSelected()));

        HBox darkThemeRow = new HBox(20, darkThemeLabel, darkThemeToggle);
        darkThemeRow.setAlignment(Pos.CENTER_LEFT);
        darkThemeRow.setMaxWidth(Double.MAX_VALUE);
        darkThemeRow.getStyleClass().add("settings-row");

        Separator divider = new Separator();
        divider.setMaxWidth(Double.MAX_VALUE);

        languageTitle.getStyleClass().add("heading-lg");

        languageDescription.getStyleClass().add("settings-language-description");

        languageSelector.setValue(Language.ENGLISH);
        languageSelector.setPrefWidth(230);
        languageSelector.getStyleClass().add("settings-language-selector");
        languageSelector.setAccessibleText("Application language");
        languageSelector.setOnAction(event -> {
            if (updatingLanguage) {
                return;
            }
            Language selected = languageSelector.getValue();
            if (selected != null) {
                onLanguageChanged.accept(selected);
            }
        });

        VBox languageContent = new VBox(10, languageDescription, languageSelector);
        languageContent.setMaxWidth(Double.MAX_VALUE);

        userGuideTitle.getStyleClass().add("heading-lg");

        userGuideLabel.getStyleClass().add("heading-md");
        HBox.setHgrow(userGuideLabel, Priority.ALWAYS);

        ToggleSwitch userGuideToggle = new ToggleSwitch();
        userGuideToggle.setSelected(UserGuideSettings.isShownOnStartup());
        userGuideToggle.setOnAction(event -> UserGuideSettings.setShownOnStartup(userGuideToggle.isSelected()));

        HBox userGuideRow = new HBox(20, userGuideLabel, userGuideToggle);
        userGuideRow.setAlignment(Pos.CENTER_LEFT);
        userGuideRow.setMaxWidth(Double.MAX_VALUE);
        userGuideRow.getStyleClass().add("settings-row");

        Separator languageDivider = new Separator();
        languageDivider.setMaxWidth(Double.MAX_VALUE);

        getChildren().addAll(
                title,
                subtitle,
                appearanceTitle,
                darkThemeRow,
                divider,
                languageTitle,
                languageContent,
                languageDivider,
                userGuideTitle,
                userGuideRow);

        refreshText(LocalizationService.getInstance());
    }

    public void setOnLanguageChanged(Consumer<Language> callback) {
        onLanguageChanged = callback != null ? callback : language -> {
        };
    }

    public Language getSelectedLanguage() {
        return languageSelector.getValue();
    }

    public void setSelectedLanguage(Language language) {
        if (language == null)
            return;
        updatingLanguage = true;
        try {
            languageSelector.setValue(language);
        } finally {
            updatingLanguage = false;
        }
    }

    public void refreshText(LocalizationService localizationService) {
        title.setText(localizationService.get("settings"));
        subtitle.setText(localizationService.get("settings.subtitle"));
        appearanceTitle.setText(localizationService.get("settings.appearance"));
        darkThemeLabel.setText(localizationService.get("settings.darkTheme"));
        languageTitle.setText(localizationService.get("settings.language"));
        languageDescription.setText(localizationService.get("settings.languageDescription"));
        userGuideTitle.setText(localizationService.get("settings.additional"));
        userGuideLabel.setText(localizationService.get("settings.showUserTour"));
    }

    public Label getTitleLabel() {
        return title;
    }

    public Label getSubtitleLabel() {
        return subtitle;
    }

    public Label getDarkThemeLabel() {
        return darkThemeLabel;
    }

    public Label getLanguageDescriptionLabel() {
        return languageDescription;
    }
}
