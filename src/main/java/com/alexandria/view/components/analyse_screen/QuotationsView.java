package com.alexandria.view.components.analyse_screen;

import com.alexandria.model.Quotation;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.function.Consumer;

public class QuotationsView extends VBox {
        private final GridPane cardsContainer = new GridPane();
        private Consumer<Quotation> onGoTo = quotation -> {
        };
        private Consumer<Quotation> onDelete = quotation -> {
        };
        private Consumer<Quotation> onEdit = quotation -> {
        };

        public QuotationsView() {
                getStyleClass().add("quotations-view");

                setPadding(new Insets(24));

                cardsContainer.setAlignment(Pos.TOP_LEFT);
                cardsContainer.setHgap(16);
                cardsContainer.setVgap(24);

                ColumnConstraints firstColumn = new ColumnConstraints();
                firstColumn.setMinWidth(280);
                firstColumn.setPrefWidth(280);
                firstColumn.setHgrow(Priority.NEVER);

                ColumnConstraints secondColumn = new ColumnConstraints();
                secondColumn.setMinWidth(280);
                secondColumn.setPrefWidth(280);
                secondColumn.setHgrow(Priority.NEVER);

                cardsContainer.getColumnConstraints().addAll(firstColumn, secondColumn);
                ScrollPane scroll = new ScrollPane(cardsContainer);

                scroll.setFitToWidth(false);
                scroll.setFitToHeight(false);
                scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
                scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
                scroll.getStyleClass().add("shared-scroll");

                VBox.setVgrow(scroll, Priority.ALWAYS);
                getChildren().add(scroll);
        }

        public void setQuotations(List<Quotation> quotations) {
                cardsContainer.getChildren().clear();

                if (quotations == null || quotations.isEmpty()) {
                        Label empty = new Label("No quotations yet.");
                        empty.getStyleClass().add("text-muted");

                        cardsContainer.setAlignment(Pos.TOP_LEFT);
                        cardsContainer.add(empty, 0, 0);

                        return;
                }

                cardsContainer.setAlignment(Pos.TOP_LEFT);

                for (int i = 0; i < quotations.size(); i++) {
                        Quotation quotation = quotations.get(i);

                        QuotationCard card = new QuotationCard(quotation);

                        card.getGoToButton().setOnAction(event -> onGoTo.accept(quotation));
                        card.getDeleteButton().setOnAction(event -> onDelete.accept(quotation));
                        card.getEditButton().setOnAction(event -> onEdit.accept(quotation));

                        int column = i % 2;
                        int row = i / 2;

                        cardsContainer.add(card, column, row);
                }
        }

        public void setOnGoTo(Consumer<Quotation> handler) {
                onGoTo = handler == null ? quotation -> {
                } : handler;
        }

        public void setOnDelete(Consumer<Quotation> handler) {
                onDelete = handler == null ? quotation -> {
                } : handler;
        }

        public void setOnEdit(Consumer<Quotation> handler) {
                onEdit = handler == null ? quotation -> {
                } : handler;
        }
}
