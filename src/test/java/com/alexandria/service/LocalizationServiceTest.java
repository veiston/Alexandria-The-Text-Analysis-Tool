package com.alexandria.service;

import java.time.LocalDate;
import java.util.Locale;
import java.util.MissingResourceException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;

public class LocalizationServiceTest {

    private LocalizationService service;

    @Before
    public void setUp() {
        service = new LocalizationService();
    }

    @Test
    public void testDefaultLocaleIsEnglish() {
        assertEquals(LocalizationService.ENGLISH, service.getLocale());
    }

    @Test
    public void testKeyLookupInEnglish() {
        assertEquals("Library", service.get("library"));
        assertEquals("Settings", service.get("settings"));
    }

    @Test
    public void testKeyLookupInJapanese() {
        service.setLocale(LocalizationService.JAPANESE);
        assertEquals(LocalizationService.JAPANESE, service.getLocale());
        assertEquals("ライブラリ", service.get("library"));
        assertEquals("設定", service.get("settings"));
    }

    @Test
    public void testKeyLookupInRussian() {
        service.setLocale(LocalizationService.RUSSIAN);
        assertEquals(LocalizationService.RUSSIAN, service.getLocale());
        assertEquals("Библиотека", service.get("library"));
        assertEquals("Настройки", service.get("settings"));
    }

    @Test(expected = MissingResourceException.class)
    public void testMissingKeyThrows() {
        service.get("nonexistent.key.for.testing");
    }

    @Test(expected = MissingResourceException.class)
    public void testMissingBundleThrows() {
        service.setLocale(new Locale("xx", "XX"));
    }

    @Test
    public void testFailedLocaleChangePreservesPreviousLocaleAndFormatting() {
        assertEquals(LocalizationService.ENGLISH, service.getLocale());
        assertEquals("Library", service.get("library"));
        String englishFormattedNumber = service.formatNumber(1234567.89);

        try {
            service.setLocale(new Locale("xx", "XX"));
        } catch (MissingResourceException expected) {
            // Expected failure to load bundle
        }

        assertEquals(LocalizationService.ENGLISH, service.getLocale());
        assertEquals("Library", service.get("library"));
        assertEquals(englishFormattedNumber, service.formatNumber(1234567.89));
    }

    @Test
    public void testFormatNumberEnglish() {
        assertEquals("1,234,567.89", service.formatNumber(1234567.89));
        assertEquals("1,234,567", service.formatNumber(1234567L));
    }

    @Test
    public void testFormatNumberJapanese() {
        service.setLocale(LocalizationService.JAPANESE);
        assertEquals("1,234,567.89", service.formatNumber(1234567.89));
        assertEquals("1,234,567", service.formatNumber(1234567L));
    }

    @Test
    public void testFormatNumberRussian() {
        service.setLocale(LocalizationService.RUSSIAN);
        assertEquals("1\u00A0234\u00A0567,89", service.formatNumber(1234567.89));
        assertEquals("1\u00A0234\u00A0567", service.formatNumber(1234567L));
    }

    @Test
    public void testFormatDateEnglish() {
        LocalDate date = LocalDate.of(2026, 10, 30);
        assertEquals("Oct 30, 2026", service.formatDate(date));
    }

    @Test
    public void testFormatDateJapanese() {
        service.setLocale(LocalizationService.JAPANESE);
        LocalDate date = LocalDate.of(2026, 10, 30);
        assertEquals("2026/10/30", service.formatDate(date));
    }

    @Test
    public void testFormatDateRussian() {
        service.setLocale(LocalizationService.RUSSIAN);
        LocalDate date = LocalDate.of(2026, 10, 30);
        assertEquals("30 окт. 2026\u202Fг.", service.formatDate(date));
    }
}
