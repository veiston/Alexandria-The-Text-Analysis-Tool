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
    private final VBox selection = new VBox(16);
    private Text firstText;
    private BiConsumer<Text, Text> onCompare = (firstText, secondText) -> {};
    private Consumer<Text> onAddNewText = text -> {};
    private Runnable onCancel = () -> {};

    public ComparisonTextModal() {
        LocalizationService i18n = LocalizationService.getInstance();
        getStyleClass().add("modal-card");
        setSpacing(16);
        setPadding(new Insets(24));
        setPrefWidth(640);
        setPrefHeight(600);

        title.getStyleClass().add("heading-lg");
        Label subtitle = new Label(i18n.get("library.chooseSecondText"));
        subtitle.getStyleClass().add("text-muted");

        Button cancelButton = new Button(i18n.get("cancel"));
        cancelButton.getStyleClass().addAll("button", "secondary");
        cancelButton.setMaxWidth(Double.MAX_VALUE);
        cancelButton.setOnAction(e -> onCancel.run());

        Button addNewTextButton = new Button(i18n.get("library.addNewText"));
        addNewTextButton.getStyleClass().addAll("button", "primary");
        addNewTextButton.setMaxWidth(Double.MAX_VALUE);
        addNewTextButton.setOnAction(e -> onAddNewText.accept(firstText));

        getChildren().addAll(new VBox(4, title, subtitle), selection, addNewTextButton, cancelButton);
        VBox.setVgrow(selection, Priority.ALWAYS);
    }

    public void setTexts(Text firstText, List<Text> texts) {
        LocalizationService i18n = LocalizationService.getInstance();
        this.firstText = firstText;
        title.setText(i18n.get("library.comparisonTitle").replace("{title}", titleText(firstText)));

        FlowPane textCards = new FlowPane(16, 16);
        for (Text secondText : texts) {
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
