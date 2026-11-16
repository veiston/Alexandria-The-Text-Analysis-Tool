package com.alexandria.utils;

import com.alexandria.model.Language;
import java.util.prefs.Preferences;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class LanguageSettingsTest {
    private Preferences preferences;

    @Before
    public void setUp() {
        preferences = Preferences.userNodeForPackage(LanguageSettings.class);
        preferences.remove("language");
    }

    @After
    public void cleanUp() {
        preferences.remove("language");
    }

    @Test
    public void usesEnglishByDefault() {
        assertEquals(Language.ENGLISH, LanguageSettings.getLanguage());
    }

    @Test
    public void savesLanguage() {
        LanguageSettings.setLanguage(Language.RUSSIAN);

        assertEquals(Language.RUSSIAN, LanguageSettings.getLanguage());
    }
}
