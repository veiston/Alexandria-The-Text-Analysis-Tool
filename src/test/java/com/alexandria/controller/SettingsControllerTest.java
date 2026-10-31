package com.alexandria.controller;

import java.time.LocalDate;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

import com.alexandria.model.Language;
import com.alexandria.service.LocalizationService;
import com.alexandria.utils.LanguageSettings;
import com.alexandria.view.screens.SettingsScreen;

import javafx.geometry.NodeOrientation;

/**
 * Controller tests for SettingsController and SettingsScreen localization.
 */
public class SettingsControllerTest extends JavaFxTestBase {

    private LocalizationService localizationService;
    private SettingsScreen settingsScreen;
    private LanguageSettingsController controller;

    @Before
    public void setUp() throws Exception {
        LanguageSettings.setLanguage(Language.ENGLISH);
        runOnFxThread(() -> {
            localizationService = new LocalizationService();
            settingsScreen = new SettingsScreen();
            controller = new LanguageSettingsController(localizationService, settingsScreen);
        });
    }
    @Test
    public void testLanguageToLocaleMapping() {
        assertEquals(Locale.US, Language.ENGLISH.getLocale());
        assertEquals(Locale.JAPAN, Language.JAPANESE.getLocale());
        assertEquals(new Locale("ru", "RU"), Language.RUSSIAN.getLocale());
    }
    @Test
    public void testSelectingEnglishCallsServiceAndUpdatesUI() throws Exception {
        runOnFxThread(() -> {
            controller.applyLanguage(Language.ENGLISH);
            assertEquals(LocalizationService.ENGLISH, localizationService.getLocale());
            assertEquals(Language.ENGLISH, settingsScreen.getSelectedLanguage());

            // User-facing text uses translation keys in the bundle
            assertEquals(localizationService.get("settings"), settingsScreen.getTitleLabel().getText());
            assertEquals(localizationService.get("settings.subtitle"), settingsScreen.getSubtitleLabel().getText());
            assertEquals(localizationService.get("settings.darkTheme"), settingsScreen.getDarkThemeLabel().getText());
            assertEquals(localizationService.get("settings.languageDescription"), settingsScreen.getLanguageDescriptionLabel().getText());
            assertEquals("Settings", settingsScreen.getTitleLabel().getText());
        });
    }

    @Test
    public void testSelectingJapaneseCallsServiceAndUpdatesUI() throws Exception {
        runOnFxThread(() -> {
            controller.applyLanguage(Language.JAPANESE);
            assertEquals(LocalizationService.JAPANESE, localizationService.getLocale());
            assertEquals(Language.JAPANESE, settingsScreen.getSelectedLanguage());

            assertEquals(localizationService.get("settings"), settingsScreen.getTitleLabel().getText());
            assertEquals("設定", settingsScreen.getTitleLabel().getText());
            assertEquals("アプリケーション設定を管理", settingsScreen.getSubtitleLabel().getText());
            assertEquals("ダークテーマ", settingsScreen.getDarkThemeLabel().getText());
            assertEquals("アプリケーション全体で使用する言語を選択してください。", settingsScreen.getLanguageDescriptionLabel().getText());
        });
    }

    @Test
    public void testSelectingRussianCallsServiceAndUpdatesUI() throws Exception {
        runOnFxThread(() -> {
            controller.applyLanguage(Language.RUSSIAN);
            assertEquals(LocalizationService.RUSSIAN, localizationService.getLocale());
            assertEquals(Language.RUSSIAN, settingsScreen.getSelectedLanguage());

            // UI text updated to Russian without restart
            assertEquals(localizationService.get("settings"), settingsScreen.getTitleLabel().getText());
            assertEquals("Настройки", settingsScreen.getTitleLabel().getText());
            assertEquals("Управляйте настройками приложения", settingsScreen.getSubtitleLabel().getText());
            assertEquals("Тёмная тема", settingsScreen.getDarkThemeLabel().getText());
            assertEquals("Выберите язык, используемый в приложении.", settingsScreen.getLanguageDescriptionLabel().getText());
        });
    }

    /**
     * The selected language is retained between launches.
     */
    @Test
    public void testSelectedLanguageIsRetainedBetweenLaunches() throws Exception {
        runOnFxThread(() -> {
            controller.applyLanguage(Language.RUSSIAN);
            assertEquals(Language.RUSSIAN, LanguageSettings.getLanguage());

            // Simulate next launch
            LocalizationService nextService = new LocalizationService();
            SettingsScreen nextScreen = new SettingsScreen();
            LanguageSettingsController nextController = new LanguageSettingsController(nextService, nextScreen);

            assertEquals(Language.RUSSIAN, nextScreen.getSelectedLanguage());
            assertEquals(LocalizationService.RUSSIAN, nextService.getLocale());
            assertEquals("Настройки", nextScreen.getTitleLabel().getText());

            // Reset back to English
            nextController.applyLanguage(Language.ENGLISH);
            assertEquals(Language.ENGLISH, LanguageSettings.getLanguage());
        });
    }

    /**
     * Verify Russian and Japanese locale-formatted dates and numbers (used across screens and tables).
     */
    @Test
    public void testRussianAndJapaneseNumberAndDateFormatting() {
        LocalDate date = LocalDate.of(2026, 10, 31);
        double number = 1234567.89;

        // Japanese formatting
        localizationService.setLocale(LocalizationService.JAPANESE);
        assertEquals("2026/10/31", localizationService.formatDate(date));
        assertEquals("1,234,567.89", localizationService.formatNumber(number));

        // Russian formatting
        localizationService.setLocale(LocalizationService.RUSSIAN);
        assertEquals("31 окт. 2026\u202Fг.", localizationService.formatDate(date));
        assertEquals("1\u00A0234\u00A0567,89", localizationService.formatNumber(number));

        // English formatting
        localizationService.setLocale(LocalizationService.ENGLISH);
        assertEquals("Oct 31, 2026", localizationService.formatDate(date));
        assertEquals("1,234,567.89", localizationService.formatNumber(number));
    }
    @Test
    public void testLtrAndRtlLayoutBehavior() throws Exception {
        runOnFxThread(() -> {
            assertEquals(NodeOrientation.INHERIT, settingsScreen.getNodeOrientation());

            settingsScreen.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
            assertEquals(NodeOrientation.RIGHT_TO_LEFT, settingsScreen.getNodeOrientation());

            settingsScreen.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
            assertEquals(NodeOrientation.LEFT_TO_RIGHT, settingsScreen.getNodeOrientation());
        });
    }
}
