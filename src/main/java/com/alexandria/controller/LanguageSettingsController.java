package com.alexandria.controller;

import com.alexandria.model.Language;
import com.alexandria.model.User;
import com.alexandria.dao.UserDAO;
import com.alexandria.service.LocalizationService;
import com.alexandria.utils.LanguageSettings;
import com.alexandria.view.screens.SettingsScreen;

public class LanguageSettingsController {

    private final LocalizationService localizationService;
    private final SettingsScreen settingsScreen;

    private final UserDAO userDAO;
    private User user;

    public LanguageSettingsController(LocalizationService localizationService, SettingsScreen settingsScreen, UserDAO userDAO, User user) {
        this.localizationService = localizationService;
        this.settingsScreen = settingsScreen;
        this.userDAO = userDAO;
        this.user = user;

        settingsScreen.refreshText(localizationService);
        localizationService.apply(settingsScreen);

        this.settingsScreen.setOnLanguageChanged(this::applyLanguage);
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void applyLanguage(Language language) {
        if (language == null) {
            return;
        }
        LanguageSettings.setLanguage(language);

        saveUserLangToDB(language);

        localizationService.setLocale(language.getLocale());
        settingsScreen.refreshText(localizationService);
        localizationService.apply(settingsScreen);
    }

    private void saveUserLangToDB(Language language) {
        if (user != null) {
			try {
				userDAO.updateLocale(user.getId(), language);
				user.setLocale(language);
			} catch (java.sql.SQLException exception) {
				System.err.println(localizationService.get("sm.couldNotSaveUserLanguage"));
			}
		}
    }

    public LocalizationService getLocalizationService() {
        return localizationService;
    }

    public SettingsScreen getSettingsScreen() {
        return settingsScreen;
    }
}
