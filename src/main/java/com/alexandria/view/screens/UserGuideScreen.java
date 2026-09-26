package com.alexandria.view.screens;

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

    private final Toggle sectionToggle = new Toggle(
            "New Project", "Library", "Analyze", "Compare", "Archive", "Profile", "Settings");
    private final VBox sectionContent = new VBox(24);

    public UserGuideScreen() {
        getStyleClass().add("user-guide-screen");
        setSpacing(20);
        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        VBox description = buildDescription();

        sectionToggle.getStyleClass().add("user-guide-tabs");
        sectionToggle.setMaxWidth(Double.MAX_VALUE);
        sectionToggle.setOnToggle(this::showSection);

        sectionContent.getStyleClass().add("user-guide-content");
        ScrollPane scrollPane = new ScrollPane(sectionContent);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("shared-scroll");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        getChildren().addAll(buildHeader(), description, sectionToggle, scrollPane);
        showSection(0);
    }

    private HBox buildHeader() {
        Label title = new Label("User Guide");
        title.getStyleClass().add("heading-xl");

        Label subtitle = new Label("Guidelines for using the application");
        subtitle.getStyleClass().addAll("text-muted", "user-guide-subtitle");

        VBox titleBox = new VBox(6, title, subtitle);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox header = new HBox(titleBox, spacer, new UserGuideTourButton());
        header.setAlignment(Pos.CENTER_LEFT);
        header.getStyleClass().add("user-guide-header");
        return header;
    }

    private VBox buildDescription() {
        VBox description = new VBox(8);
        for (String text : UserGuideData.ABOUT_PARAGRAPHS) {
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
        return description;
    }

    private void showSection(int index) {
        Node section = switch (index) {
            case 0 -> new UserGuideSection(
                    new UserGuideSectionContent(UserGuideData.NEW_PROJECT_STEPS, null),
                    "/images/user-guide/new-project.png");
            case 1 -> new UserGuideSection(
                    new UserGuideSectionContent(UserGuideData.LIBRARY_STEPS, null),
                    "/images/user-guide/library.png");
            case 2 -> buildAnalyzeGuide();
            case 3 -> buildCompareGuide();
            case 4 -> new UserGuideSection(
                    new UserGuideSectionContent(UserGuideData.ARCHIVE_STEPS, null),
                    "/images/user-guide/archive.png");
            case 5 -> new UserGuideSection(
                    new UserGuideSectionContent(UserGuideData.PROFILE_STEPS, null),
                    "/images/user-guide/profile.png");
            case 6 -> new UserGuideSection(
                    new UserGuideSectionContent(UserGuideData.SETTINGS_STEPS, UserGuideData.SETTINGS_NOTE),
                    "/images/user-guide/settings.png");
            default -> new VBox();
        };
        sectionContent.getChildren().setAll(section);
    }

    private Node buildAnalyzeGuide() {
        Label startTitle = guideHeading("Open a text for analysis");
        Label startDescription = new Label(UserGuideData.ANALYZE_INTRODUCTION);
        startDescription.getStyleClass().add("user-guide-row-text");
        startDescription.setWrapText(true);
        startDescription.setMinWidth(0);
        startDescription.setMaxWidth(Double.MAX_VALUE);

        Label functionsTitle = guideHeading("How analysis results are calculated");

        VBox functionList = new VBox(10);
        for (int i = 0; i < UserGuideData.ANALYZE_METHODS.size(); i++) {
            functionList.getChildren().add(new UserGuideStep(i + 1, UserGuideData.ANALYZE_METHODS.get(i)));
        }

        VBox content = new VBox(12, startTitle, startDescription, functionsTitle, functionList);
        content.setMinWidth(0);
        return new UserGuideSection(content, "/images/user-guide/analyze.png");
    }

    private VBox buildCompareGuide() {
		// TODO Add guide content for the text comparison screen
        Label todo = new Label("TODO");
        todo.getStyleClass().addAll("text-muted", "user-guide-note");
        return new VBox(todo);
    }

    private Label guideHeading(String text) {
        Label heading = new Label(text);
        heading.getStyleClass().addAll("heading-md", "user-guide-section-heading");
        return heading;
    }

}
