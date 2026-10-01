package com.alexandria.view.components.library_screen;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.alexandria.model.Text;
import com.alexandria.view.components.shared.Card;
import com.alexandria.view.components.shared.EmptyState;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class ComparisonTextModal extends VBox {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("d.MM.uuuu");

    private final Label title = new Label();
    private final VBox selection = new VBox(16);
    private Text firstText;
    private BiConsumer<Text, Text> onCompare = (firstText, secondText) -> {};
    private Consumer<Text> onAddNewText = text -> {};
    private Runnable onCancel = () -> {};

    public ComparisonTextModal() {
        getStyleClass().add("modal-card");
        setSpacing(16);
        setPadding(new Insets(24));
        setPrefWidth(640);
        setPrefHeight(600);

        title.getStyleClass().add("heading-lg");
        Label subtitle = new Label("Choose a second text from your library");
        subtitle.getStyleClass().add("text-muted");

        Button cancelButton = new Button("Cancel");
        cancelButton.getStyleClass().addAll("button", "secondary");
        cancelButton.setMaxWidth(Double.MAX_VALUE);
        cancelButton.setOnAction(e -> onCancel.run());

        Button addNewTextButton = new Button("Add a new text");
        addNewTextButton.getStyleClass().addAll("button", "primary");
        addNewTextButton.setMaxWidth(Double.MAX_VALUE);
        addNewTextButton.setOnAction(e -> onAddNewText.accept(firstText));

        getChildren().addAll(new VBox(4, title, subtitle), selection, addNewTextButton, cancelButton);
        VBox.setVgrow(selection, Priority.ALWAYS);
    }

    public void setTexts(Text firstText, List<Text> texts) {
        this.firstText = firstText;
        title.setText("Compare \"" + titleText(firstText) + "\" with:");

        FlowPane textCards = new FlowPane(16, 16);
        for (Text secondText : texts) {
            if (!Objects.equals(firstText.getId(), secondText.getId())) {
                textCards.getChildren().add(createTextCard(firstText, secondText));
            }
        }

        if (textCards.getChildren().isEmpty()) {
            selection.getChildren().setAll(new EmptyState(
                    "No other texts to compare",
                    "Add another text to your library first"));
            return;
        }

        ScrollPane cardsScroll = new ScrollPane(textCards);
        cardsScroll.getStyleClass().add("shared-scroll");
        cardsScroll.setFitToWidth(true);
        cardsScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        cardsScroll.setPrefViewportHeight(430);

        selection.getChildren().setAll(cardsScroll);
        VBox.setVgrow(cardsScroll, Priority.ALWAYS);
    }

    public void setOnCompare(BiConsumer<Text, Text> handler) {
        if (handler != null) {
            onCompare = handler;
        }
    }

    public void setOnCancel(Runnable handler) {
        if (handler != null) {
            onCancel = handler;
        }
    }

    public void setOnAddNewText(Consumer<Text> handler) {
        if (handler != null) {
            onAddNewText = handler;
        }
    }

    private Card createTextCard(Text firstText, Text secondText) {
        Card card = new Card(titleText(secondText));
        card.getDeleteButton().setVisible(false);
        card.getDeleteButton().setManaged(false);

        if (secondText.getFileType() != null) {
            card.setTypeText(secondText.getFileType().name());
        }
        card.setSourceText(secondText.getFileName());
        if (secondText.getCreatedAt() != null) {
            card.setFooterText("Added " + DATE_FORMAT.format(secondText.getCreatedAt()));
        }

        card.setActionText("Choose as second text");
        card.getActionButton().setOnAction(e -> onCompare.accept(firstText, secondText));
        return card;
    }

    private String titleText(Text text) {
        if (text.getTitle() != null && !text.getTitle().isBlank()) {
            return text.getTitle();
        }
        return text.getFileName();
    }
}
