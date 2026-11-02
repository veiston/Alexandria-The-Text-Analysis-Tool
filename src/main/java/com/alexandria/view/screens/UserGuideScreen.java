package com.alexandria.view.screens;

import java.util.List;

import com.alexandria.service.LocalizationService;
import com.alexandria.view.components.shared.toggle.Toggle;
import com.alexandria.view.components.user_guide.UserGuideData;
import com.alexandria.view.components.user_guide.UserGuideSection;
import com.alexandria.view.components.user_guide.UserGuideSectionContent;
import com.alexandria.view.components.user_guide.UserGuideStep;
import com.alexandria.view.components.user_guide.UserGuideTourButton;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class UserGuideScreen extends VBox {

    private final Toggle sectionToggle;
    private final VBox sectionContent = new VBox(24);
    private final Label title = new Label();
    private final Label subtitle = new Label();
    private final UserGuideTourButton tourButton = new UserGuideTourButton();
    private final VBox description = new VBox(8);

    public UserGuideScreen() {
        LocalizationService i18n = LocalizationService.getInstance();
        sectionToggle = new Toggle(
                i18n.get("sidebar.newProject"),
                i18n.get("library"),
                i18n.get("analyze"),
                i18n.get("compare"),
                i18n.get("archive"),
                i18n.get("profile"),
                i18n.get("settings"));

        getStyleClass().add("user-guide-screen");
        setSpacing(20);
        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        sectionToggle.getStyleClass().add("user-guide-tabs");
        sectionToggle.setMaxWidth(Double.MAX_VALUE);
        sectionToggle.setOnToggle(this::showSection);

        sectionContent.getStyleClass().add("user-guide-content");
        ScrollPane scrollPane = new ScrollPane(sectionContent);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("shared-scroll");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        getChildren().addAll(buildHeader(), description, sectionToggle, scrollPane);

        refreshText();
        LocalizationService.getInstance().addListener(locale -> refreshText());
    }

    public void refreshText() {
        LocalizationService i18n = LocalizationService.getInstance();
        title.setText(i18n.get("userGuide"));
        subtitle.setText(i18n.get("userGuide.subtitle"));
        tourButton.refreshText();
        sectionToggle.setLabels(
                i18n.get("sidebar.newProject"),
                i18n.get("library"),
                i18n.get("analyze"),
                i18n.get("compare"),
                i18n.get("archive"),
                i18n.get("profile"),
                i18n.get("settings"));
        buildDescription();
        int selected = sectionToggle.getSelectedIndex();
        showSection(selected < 0 ? 0 : selected);
    }

    private HBox buildHeader() {
        title.getStyleClass().add("heading-xl");

        subtitle.getStyleClass().addAll("text-muted", "user-guide-subtitle");

        VBox titleBox = new VBox(6, title, subtitle);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox header = new HBox(titleBox, spacer, tourButton);
        header.setAlignment(Pos.CENTER_LEFT);
        header.getStyleClass().add("user-guide-header");
        return header;
    }

    private void buildDescription() {
        description.getChildren().clear();
        for (String text : UserGuideData.getAboutParagraphs()) {
            Text paragraph = new Text(text);
            paragraph.getStyleClass().add("user-guide-description-text");

            TextFlow paragraphFlow = new TextFlow(paragraph);
            paragraphFlow.setMinWidth(0);
            paragraphFlow.setMaxWidth(Double.MAX_VALUE);
            paragraphFlow.setLineSpacing(3);
            paragraphFlow.prefWidthProperty().bind(widthProperty().subtract(60));
            description.getChildren().add(paragraphFlow);
        }
        description.setMaxWidth(Double.MAX_VALUE);
    }

    private void showSection(int index) {
        Node section = switch (index) {
            case 0 -> new UserGuideSection(
                    new UserGuideSectionContent(UserGuideData.getNewProjectSteps(), null),
                    "/images/user-guide/new-project.png");
            case 1 -> new UserGuideSection(
                    new UserGuideSectionContent(UserGuideData.getLibrarySteps(), null),
                    "/images/user-guide/library.png");
            case 2 -> buildAnalyzeGuide();
            case 3 -> buildCompareGuide();
            case 4 -> new UserGuideSection(
                    new UserGuideSectionContent(UserGuideData.getArchiveSteps(), null),
                    "/images/user-guide/archive.png");
            case 5 -> new UserGuideSection(
                    new UserGuideSectionContent(UserGuideData.getProfileSteps(), null),
                    "/images/user-guide/profile.png");
            case 6 -> new UserGuideSection(
                    new UserGuideSectionContent(UserGuideData.getSettingsSteps(), UserGuideData.getSettingsNote()),
                    "/images/user-guide/settings.png");
            default -> new VBox();
        };
        sectionContent.getChildren().setAll(section);
    }

    private Node buildAnalyzeGuide() {
        LocalizationService i18n = LocalizationService.getInstance();
        Label startTitle = guideHeading(i18n.get("userGuide.analyzeTitle"));
        Label startDescription = new Label(UserGuideData.getAnalyzeIntroduction());
        startDescription.getStyleClass().add("user-guide-row-text");
        startDescription.setWrapText(true);
        startDescription.setMinWidth(0);
        startDescription.setMaxWidth(Double.MAX_VALUE);

        Label functionsTitle = guideHeading(i18n.get("userGuide.analyzeMethodsTitle"));

        VBox functionList = new VBox(10);
        List<String> methods = UserGuideData.getAnalyzeMethods();
        for (int i = 0; i < methods.size(); i++) {
            functionList.getChildren().add(new UserGuideStep(i + 1, methods.get(i)));
        }

        VBox content = new VBox(12, startTitle, startDescription, functionsTitle, functionList);
        content.setMinWidth(0);
        return new UserGuideSection(content, "/images/user-guide/analyze.png");
    }

    private Node buildCompareGuide() {
        LocalizationService i18n = LocalizationService.getInstance();
        Label startTitle = guideHeading(i18n.get("userGuide.compareTitle"));
        Label startDescription = new Label(UserGuideData.getCompareIntroduction());
        startDescription.getStyleClass().add("user-guide-row-text");
        startDescription.setWrapText(true);
        startDescription.setMinWidth(0);
        startDescription.setMaxWidth(Double.MAX_VALUE);

        Label functionsTitle = guideHeading(i18n.get("userGuide.compareMethodsTitle"));

        VBox functionList = new VBox(10);
        List<String> methods = UserGuideData.getCompareMethods();
        for (int i = 0; i < methods.size(); i++) {
            functionList.getChildren().add(new UserGuideStep(i + 1, methods.get(i)));
        }

        VBox content = new VBox(12, startTitle, startDescription, functionsTitle, functionList);
        content.setMinWidth(0);
        return new UserGuideSection(content, null);
    }

    private Label guideHeading(String text) {
        Label heading = new Label(text);
        heading.getStyleClass().addAll("heading-md", "user-guide-section-heading");
        return heading;
    }

}
