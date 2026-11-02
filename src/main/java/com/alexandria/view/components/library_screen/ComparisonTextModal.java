package com.alexandria.view.components.library_screen;

import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.alexandria.model.Text;
import com.alexandria.service.LocalizationService;
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

    private final Label title = new Label();
    private final Label subtitle = new Label();
    private final VBox selection = new VBox(16);
    private final Button cancelButton = new Button();
    private final Button addNewTextButton = new Button();
    private Text firstText;
    private List<Text> texts = List.of();
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
        subtitle.getStyleClass().add("text-muted");

        cancelButton.getStyleClass().addAll("button", "secondary");
        cancelButton.setMaxWidth(Double.MAX_VALUE);
        cancelButton.setOnAction(e -> onCancel.run());

        addNewTextButton.getStyleClass().addAll("button", "primary");
        addNewTextButton.setMaxWidth(Double.MAX_VALUE);
        addNewTextButton.setOnAction(e -> onAddNewText.accept(firstText));

        getChildren().addAll(new VBox(4, title, subtitle), selection, addNewTextButton, cancelButton);
        VBox.setVgrow(selection, Priority.ALWAYS);

        LocalizationService.getInstance().addListener(locale -> refreshText());
    }

    public void setTexts(Text firstText, List<Text> texts) {
        this.firstText = firstText;
        this.texts = texts == null ? List.of() : List.copyOf(texts);
        refreshText();
    }

    private void refreshText() {
        LocalizationService i18n = LocalizationService.getInstance();
        subtitle.setText(i18n.get("library.chooseSecondText"));
        addNewTextButton.setText(i18n.get("library.addNewText"));
        cancelButton.setText(i18n.get("cancel"));

        if (firstText == null) {
            return;
        }

        title.setText(i18n.get("library.comparisonTitle").replace("{title}", titleText(firstText)));

        FlowPane textCards = new FlowPane(16, 16);
        for (Text secondText : this.texts) {
            if (!Objects.equals(firstText.getId(), secondText.getId())) {
                textCards.getChildren().add(createTextCard(firstText, secondText));
            }
        }

        if (textCards.getChildren().isEmpty()) {
            selection.getChildren().setAll(new EmptyState(
                    i18n.get("library.noOtherTexts"),
                    i18n.get("library.addAnotherText")));
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
        LocalizationService i18n = LocalizationService.getInstance();
        Card card = new Card(titleText(secondText));
        card.getDeleteButton().setVisible(false);
        card.getDeleteButton().setManaged(false);

        if (secondText.getFileType() != null) {
            card.setTypeText(secondText.getFileType().name());
        }
        card.setSourceText(secondText.getFileName());
        if (secondText.getCreatedAt() != null) {
            String dateText = i18n.formatDate(secondText.getCreatedAt().toLocalDate());
            card.setFooterText(i18n.get("added").replace("{date}", dateText));
        }

        card.setActionText(i18n.get("library.chooseAsSecondText"));
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
