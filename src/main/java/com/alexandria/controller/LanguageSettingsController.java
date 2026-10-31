package com.alexandria.controller;

import com.alexandria.model.Language;
import com.alexandria.service.LocalizationService;
import com.alexandria.utils.LanguageSettings;
import com.alexandria.view.screens.SettingsScreen;

public class LanguageSettingsController {

    private final LocalizationService localizationService;
    private final SettingsScreen settingsScreen;

    public LanguageSettingsController(LocalizationService localizationService, SettingsScreen settingsScreen) {
        this.localizationService = localizationService;
        this.settingsScreen = settingsScreen;

        Language saved = LanguageSettings.getLanguage();
        applyLanguage(saved);

        this.settingsScreen.setOnLanguageChanged(this::applyLanguage);
    }

    public void applyLanguage(Language language) {
        if (language == null) {
            return;
        }
        localizationService.setLocale(language.getLocale());
        LanguageSettings.setLanguage(language);
        settingsScreen.setSelectedLanguage(language);
        settingsScreen.refreshText(localizationService);
    }

    public LocalizationService getLocalizationService() {
        return localizationService;
    }

    public SettingsScreen getSettingsScreen() {
        return settingsScreen;
    }
}
