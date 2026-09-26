package com.alexandria.view.components.user_guide;

import java.util.List;

import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class UserGuideSectionContent extends VBox {

    public UserGuideSectionContent(List<String> steps, String noteText) {
        setSpacing(10);

        for (int i = 0; i < steps.size(); i++) {
            getChildren().add(new UserGuideStep(i + 1, steps.get(i)));
        }

        if (noteText != null) {
            Region spacer = new Region();
            VBox.setVgrow(spacer, Priority.ALWAYS);

            Label note = new Label(noteText);
            note.getStyleClass().addAll("text-muted", "user-guide-note");
            note.setWrapText(true);
            note.setMinWidth(0);
            note.setMaxWidth(Double.MAX_VALUE);
            getChildren().addAll(spacer, note);
        }

        setMinWidth(0);
        setMaxHeight(Double.MAX_VALUE);
    }
}
