package com.alexandria.view.screens;

import com.alexandria.utils.ThemeSettings;
import com.alexandria.utils.UserGuideSettings;
import com.alexandria.view.components.shared.ToggleSwitch;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class SettingsScreen extends VBox {

    public SettingsScreen() {
        setPadding(new Insets(30));
        setSpacing(16);
        setMaxWidth(Double.MAX_VALUE);

        Label title = new Label("Settings");
        title.getStyleClass().add("heading-xl");

        Label subtitle = new Label("Manage your application preferences.");
        subtitle.getStyleClass().add("text-muted");

        Label appearanceTitle = new Label("Appearance");
        appearanceTitle.getStyleClass().add("heading-lg");

        Label darkThemeLabel = new Label("Dark theme");
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

        Label languageTitle = new Label("Language");
        languageTitle.getStyleClass().add("heading-lg");

        Label languageStatus = new Label(
                "Language settings are under development and will be available later.");
        languageStatus.getStyleClass().add("text-muted");

        Label userGuideTitle = new Label("Additional Dettings");
        userGuideTitle.getStyleClass().add("heading-lg");

        Label userGuideLabel = new Label("Show user tour on next launch");
        userGuideLabel.getStyleClass().add("heading-md");
        HBox.setHgrow(userGuideLabel, Priority.ALWAYS);

        ToggleSwitch userGuideToggle = new ToggleSwitch();
        userGuideToggle.setSelected(UserGuideSettings.isShownOnStartup());
        userGuideToggle.setOnAction(event ->
                UserGuideSettings.setShownOnStartup(userGuideToggle.isSelected()));

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
                languageStatus,
                languageDivider,
                userGuideTitle,
                userGuideRow);
    }
}
