package com.alexandria.view.screens;

import com.alexandria.view.components.shared.toggle.Toggle;

import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Button;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/** A short, task-oriented guide for the sections available in the application. */
public class UserGuideScreen extends VBox {

    private final Toggle sectionToggle = new Toggle(
            "New Project", "Library", "Analyze", "Compare", "Archive", "Profile", "Settings");
    private final VBox guideContent = new VBox(24);

    public UserGuideScreen() {
        getStyleClass().add("user-guide-screen");
        setSpacing(20);
        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        Label title = new Label("User Guide");
        title.getStyleClass().add("heading-xl");

        Label subtitle = new Label("A quick guide to the main areas of the application");
        subtitle.getStyleClass().addAll("text-muted", "user-guide-subtitle");

        VBox header = new VBox(6, title, subtitle);
        header.getStyleClass().add("user-guide-header");

        VBox aboutProject = buildAboutProject();

        sectionToggle.getStyleClass().add("user-guide-tabs");
        sectionToggle.setMaxWidth(Double.MAX_VALUE);
        sectionToggle.setOnToggle(this::showSection);

        guideContent.getStyleClass().add("user-guide-content");
        ScrollPane scrollPane = new ScrollPane(guideContent);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("shared-scroll");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        getChildren().addAll(header, aboutProject, sectionToggle, scrollPane);
        showSection(0);
    }

    private VBox buildAboutProject() {
        TextFlow firstParagraph = aboutParagraph("Alexandria is an application for working with texts.");
        TextFlow secondParagraph = aboutParagraph(
                "You can add your own text or upload a TXT or PDF file. Alexandria can search for the words "
                        + "and phrases you need, count words and sentences, show which words are used most often, "
                        + "analyze a specific word or phrase, and compare several texts.");
        TextFlow thirdParagraph = aboutParagraph(
                "You can also save your texts, analysis results, comparisons, search results, and quotations to your account.");

        VBox aboutProject = new VBox(8, firstParagraph, secondParagraph, thirdParagraph);
        aboutProject.setMaxWidth(Double.MAX_VALUE);
        return aboutProject;
    }

    private TextFlow aboutParagraph(String text) {
        Text paragraph = new Text(text);
        paragraph.getStyleClass().add("user-guide-description-text");

        TextFlow flow = new TextFlow(paragraph);
        flow.setMinWidth(0);
        flow.setMaxWidth(Double.MAX_VALUE);
        flow.setLineSpacing(3);
        flow.prefWidthProperty().bind(widthProperty().subtract(60));
        return flow;
    }

    private void showSection(int index) {
        if (index == 0) {
            guideContent.getChildren().setAll(buildNewProjectGuide());
            return;
        }

        guideContent.getChildren().clear();
    }

    private VBox buildNewProjectGuide() {
        Label beforeButton = new Label("Click");
        beforeButton.getStyleClass().addAll("body-text", "user-guide-description");

        Button newProjectExample = new Button("+ New Project");
        newProjectExample.getStyleClass().addAll("button", "primary");
        newProjectExample.setFocusTraversable(false);

        Label afterButton = new Label("in the sidebar to add a text.");
        afterButton.getStyleClass().addAll("body-text", "user-guide-description");

        HBox instruction = new HBox(6, beforeButton, newProjectExample, afterButton);
        instruction.setAlignment(Pos.CENTER_LEFT);

        Label screenshotPlaceholder = new Label("SCREENSHOT");
        screenshotPlaceholder.getStyleClass().add("user-guide-screenshot-placeholder");

        String[] steps = {
                "Choose Upload File or Paste Text.",
                "Enter the project name.",
                "Choose where to open the project:\n\n"
                        + "• Analyze — work with one text: search for words or phrases and view text statistics.\n"
                        + "• Compare — work with several texts and compare their words and usage.",
                "Click Create Project."
        };

        VBox stepList = new VBox(10);
        for (int i = 0; i < steps.length; i++) {
            stepList.getChildren().add(numberedRow(i + 1, steps[i], "user-guide-step"));
        }

        return new VBox(18, instruction, screenshotPlaceholder, stepList);
    }

    private HBox numberedRow(int number, String text, String styleClass) {
        Label numberLabel = new Label(String.valueOf(number));
        numberLabel.getStyleClass().add("user-guide-number");

        Label textLabel = new Label(text);
        textLabel.getStyleClass().add("user-guide-row-text");
        configureWrapping(textLabel);
        HBox.setHgrow(textLabel, Priority.ALWAYS);

        HBox row = new HBox(10, numberLabel, textLabel);
        row.setAlignment(Pos.TOP_LEFT);
        row.getStyleClass().add(styleClass);
        return row;
    }

    private void configureWrapping(Label label) {
        label.setWrapText(true);
        label.setMinWidth(0);
        label.setMaxWidth(Double.MAX_VALUE);
    }
}
