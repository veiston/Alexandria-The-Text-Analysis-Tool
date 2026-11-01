package com.alexandria.view.components.user_guide;

import java.util.List;
import com.alexandria.service.LocalizationService;

public final class UserGuideData {

    public static List<String> getAboutParagraphs() {
        LocalizationService i18n = LocalizationService.getInstance();
        return List.of(
                i18n.get("userGuide.about1"),
                i18n.get("userGuide.about2"),
                i18n.get("userGuide.about3"));
    }

    public static List<String> getNewProjectSteps() {
        LocalizationService i18n = LocalizationService.getInstance();
        return List.of(
                i18n.get("userGuide.newProject1"),
                i18n.get("userGuide.newProject2"),
                i18n.get("userGuide.newProject3"),
                i18n.get("userGuide.newProject4"),
                i18n.get("userGuide.newProject5"));
    }

    public static String getAnalyzeIntroduction() {
        return LocalizationService.getInstance().get("userGuide.analyzeIntroduction");
    }

    public static List<String> getAnalyzeMethods() {
        LocalizationService i18n = LocalizationService.getInstance();
        return List.of(
                i18n.get("userGuide.analyzeMethod1"),
                i18n.get("userGuide.analyzeMethod2"),
                i18n.get("userGuide.analyzeMethod3"),
                i18n.get("userGuide.analyzeMethod4"),
                i18n.get("userGuide.analyzeMethod5"),
                i18n.get("userGuide.analyzeMethod6"));
    }

    public static String getCompareIntroduction() {
        return LocalizationService.getInstance().get("userGuide.compareIntroduction");
    }

    public static List<String> getCompareMethods() {
        LocalizationService i18n = LocalizationService.getInstance();
        return List.of(
                i18n.get("userGuide.compareMethod1"),
                i18n.get("userGuide.compareMethod2"),
                i18n.get("userGuide.compareMethod3"),
                i18n.get("userGuide.compareMethod4"),
                i18n.get("userGuide.compareMethod5"));
    }

    public static List<String> getLibrarySteps() {
        LocalizationService i18n = LocalizationService.getInstance();
        return List.of(
                i18n.get("userGuide.library1"),
                i18n.get("userGuide.library2"),
                i18n.get("userGuide.library3"),
                i18n.get("userGuide.library4"));
    }

    public static List<String> getArchiveSteps() {
        LocalizationService i18n = LocalizationService.getInstance();
        return List.of(
                i18n.get("userGuide.archive1"),
                i18n.get("userGuide.archive2"),
                i18n.get("userGuide.archive3"),
                i18n.get("userGuide.archive4"));
    }

    public static List<String> getProfileSteps() {
        LocalizationService i18n = LocalizationService.getInstance();
        return List.of(
                i18n.get("userGuide.profile1"),
                i18n.get("userGuide.profile2"),
                i18n.get("userGuide.profile3"),
                i18n.get("userGuide.profile4"));
    }

    public static List<String> getSettingsSteps() {
        LocalizationService i18n = LocalizationService.getInstance();
        return List.of(
                i18n.get("userGuide.settings1"),
                i18n.get("userGuide.settings2"));
    }

    public static String getSettingsNote() {
        return LocalizationService.getInstance().get("userGuide.settingsNote");
    }

    private UserGuideData() {
    }
}
