package com.alexandria.view.screens;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.kordamp.ikonli.javafx.FontIcon;

import com.alexandria.model.Text;
import com.alexandria.service.LocalizationService;
import com.alexandria.view.components.shared.Card;
import com.alexandria.view.components.shared.EmptyState;
import com.alexandria.view.components.shared.SearchInput;
import com.alexandria.view.components.shared.modal.ConfirmationAlert;
import com.alexandria.view.components.shared.modal.Modal;
import com.alexandria.view.components.library_screen.ComparisonTextModal;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class LibraryScreen extends StackPane {

    private final SearchInput searchInput = new SearchInput(LocalizationService.getInstance().get("library.search"));
    private final FlowPane cardsPane = new FlowPane(16, 16);
    private final StackPane contentArea = new StackPane();
    private final BorderPane libraryLayout = new BorderPane();
    private final VBox libraryBody = buildBody();
    private final Modal modal = new Modal();
    private final ComparisonTextModal comparisonTextModal = new ComparisonTextModal();
    private final HBox filterBar = new HBox(8);
    private final Label title = new Label();
    private final Label subtitle = new Label();
    private String activeFilter = "ALL";

    private List<Text> texts = List.of();

    private Consumer<Text> onOpenInAnalysis = text -> {};
    private BiConsumer<Text, Text> onCompare = (firstText, secondText) -> {};
    private Consumer<Text> onAddNewComparisonText = text -> {};
    private Consumer<Integer> onDeleteText = id -> {};
    private Runnable onNewProject = () -> {};
    private Runnable onShown = () -> {};

    public LibraryScreen() {
        getStyleClass().add("content-screen");
        searchInput.setMaxWidth(Double.MAX_VALUE);

        updateFilterButtons();
        libraryLayout.setTop(buildHeader());
        libraryLayout.setCenter(libraryBody);
        libraryLayout.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        getChildren().addAll(libraryLayout, modal);
        StackPane.setAlignment(libraryLayout, Pos.CENTER);
        StackPane.setAlignment(modal, Pos.CENTER);
        modal.prefWidthProperty().bind(widthProperty());
        modal.prefHeightProperty().bind(heightProperty());

        configureActions();
        comparisonTextModal.setOnCompare((firstText, secondText) -> {
            modal.hide();
            onCompare.accept(firstText, secondText);
        });
        comparisonTextModal.setOnCancel(modal::hide);
        comparisonTextModal.setOnAddNewText(firstText -> {
            modal.hide();
            onAddNewComparisonText.accept(firstText);
        });
        refreshText();
        LocalizationService.getInstance().addListener(locale -> refreshText());

        // Refresh when router mounts screen into active scene
        sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                onShown.run();
            }
        });

        parentProperty().addListener((observable, oldParent, newParent) -> {
            if (newParent != null) {
                onShown.run();
            }
        });
    }

    public void refreshText() {
        LocalizationService i18n = LocalizationService.getInstance();
        title.setText(i18n.get("library"));
        subtitle.setText(i18n.get("library.subtitle"));
        searchInput.setPromptText(i18n.get("library.search"));
        updateFilterButtons();
        showTexts();
    }

    private VBox buildHeader() {
        title.setText(LocalizationService.getInstance().get("library"));
        title.getStyleClass().add("heading-xl");

        subtitle.setText(LocalizationService.getInstance().get("library.subtitle"));
        subtitle.getStyleClass().addAll("text-muted", "archive-subtitle");

        VBox header = new VBox(10, title, subtitle, filterBar);
        header.getStyleClass().add("content-screen-header");
        return header;
    }

    private void updateFilterButtons() {
        filterBar.getChildren().clear();
        LocalizationService i18n = LocalizationService.getInstance();
        String[][] filterDefs = {
            {"ALL", i18n.get("all")},
            {"PDF", i18n.get("pdf")},
            {"TXT", i18n.get("txt")},
            {"MANUAL", i18n.get("manual")}
        };

        for (String[] def : filterDefs) {
            String filterKey = def[0];
            String filterLabel = def[1];
            Button btn = new Button(filterLabel);
            btn.getStyleClass().add("button");

            if (filterKey.equals(activeFilter)) {
                btn.getStyleClass().add("primary");
            } else {
                btn.getStyleClass().add("secondary");
            }

            btn.setOnAction(e -> {
                activeFilter = filterKey;
                updateFilterButtons();
                showTexts();
            });
            filterBar.getChildren().add(btn);
        }
    }

    private VBox buildBody() {
        VBox body = new VBox(contentArea);
        body.getStyleClass().add("content-screen-body");
        VBox.setVgrow(contentArea, Priority.ALWAYS);
        return body;
    }

    private void configureActions() {
        searchInput.textProperty().addListener((observable, oldText, newText) -> {
            showTexts();
        });
    }

    public void setOnOpenInAnalysis(Consumer<Text> handler) {
        if (handler != null) {
            onOpenInAnalysis = handler;
        }
    }

    public void setOnCompare(BiConsumer<Text, Text> handler) {
        if (handler != null) {
            onCompare = handler;
        }
    }

    public void setOnAddNewComparisonText(Consumer<Text> handler) {
        if (handler != null) {
            onAddNewComparisonText = handler;
        }
    }

    public void setOnDeleteText(Consumer<Integer> handler) {
        if (handler != null) {
            onDeleteText = handler;
        }
    }

    public void setOnNewProject(Runnable handler) {
        if (handler != null) {
            onNewProject = handler;
        }
    }

    public void setOnShown(Runnable handler) {
        if (handler != null) {
            onShown = handler;
        }
    }

    public void showSignInMessage() {
        LocalizationService i18n = LocalizationService.getInstance();
        EmptyState emptyState = new EmptyState(
                i18n.get("library.signInTitle"),
                i18n.get("library.signInSubtitle"));
        libraryLayout.setCenter(emptyState);
        BorderPane.setAlignment(emptyState, Pos.CENTER);
    }

    public void setTexts(List<Text> texts) {
        this.texts = List.of();
        if (texts != null) {
            this.texts = List.copyOf(texts);
        }
        showTexts();
    }

    private void showTexts() {
        libraryLayout.setCenter(libraryBody);
        cardsPane.getChildren().clear();

        boolean searching = !searchInput.getText().isBlank();

        List<Text> visibleTexts = texts.stream()
                .filter(text -> switch (activeFilter) {
                    case "PDF" -> "PDF".equalsIgnoreCase(text.getFileType() != null ? text.getFileType().name() : "");
                    case "TXT" -> "TXT".equalsIgnoreCase(text.getFileType() != null ? text.getFileType().name() : "");
                    case "MANUAL" -> text.getFileType() == null;
                    default -> true;
                })
                .filter(text -> matchesSearch(text.getTitle(), text.getFileName()))
                .toList();

        if (!searching) {
            cardsPane.getChildren().add(createNewProjectCard());
        }

        for (Text text : visibleTexts) {
            cardsPane.getChildren().add(createProjectCard(text));
        }

        StackPane libraryContent = new StackPane();

        if (searching && visibleTexts.isEmpty()) {
            EmptyState emptyState = new EmptyState(
                    emptyMessage(),
                    emptySubtitle());
            emptyState.setMaxHeight(Double.MAX_VALUE);
            libraryContent.getChildren().add(emptyState);
        } else {
            ScrollPane cardsScroll = new ScrollPane(cardsPane);
            cardsScroll.getStyleClass().add("shared-scroll");
            cardsScroll.setFitToWidth(true);
            cardsScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            cardsScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            libraryContent.getChildren().add(cardsScroll);
        }

        VBox searchAndContent = new VBox(18, searchInput, libraryContent);
        VBox.setVgrow(libraryContent, Priority.ALWAYS);

        contentArea.getChildren().setAll(searchAndContent);
    }

    private String emptyMessage() {
        LocalizationService i18n = LocalizationService.getInstance();
        if (!searchInput.getText().isBlank()) {
            return i18n.get("nothingFound");
        }
        return i18n.get("library.noSavedTexts");
    }

    private String emptySubtitle() {
        LocalizationService i18n = LocalizationService.getInstance();
        if (!searchInput.getText().isBlank()) {
            return i18n.get("tryChangingSearchQuery");
        }
        return i18n.get("library.emptySubtitle");
    }

    private Card createProjectCard(Text text) {
        LocalizationService i18n = LocalizationService.getInstance();
        Card card = new Card(titleText(text));

        if (text.getFileType() != null) {
            card.setTypeText(text.getFileType().name());
        }
        card.setSourceText(text.getFileName());

        if (text.getCreatedAt() != null) {
            String dateText = i18n.formatDate(text.getCreatedAt().toLocalDate());
            String footer = i18n.get("added").replace("{date}", dateText);
            card.setFooterText(footer);
        }

        card.setActionText(i18n.get("openWith"));
        card.getActionButton().setOnAction(e -> {
            showOpenWithModal(text);
        });

        card.getDeleteButton().setOnAction(e -> {
            if (ConfirmationAlert.show(
                    i18n.get("cm.deleteAnalysisTitle"),
                    i18n.get("cm.deleteAnalysisQuestion"),
                    i18n.get("cm.permanentActionWarning"))) {
                onDeleteText.accept(text.getId());
            }
        });

        return card;
    }

    private VBox createNewProjectCard() {
        LocalizationService i18n = LocalizationService.getInstance();
        FontIcon addIcon = new FontIcon("fas-plus-circle");
        addIcon.setIconSize(24);
        addIcon.getStyleClass().add("text-muted");

        Label title = new Label(i18n.get("library.startAnalysis"));
        title.getStyleClass().add("heading-md");

        Label subtitle = new Label(i18n.get("library.startAnalysisSubtitle"));
        subtitle.getStyleClass().add("text-muted");

        VBox card = new VBox(12, addIcon, title, subtitle);
        card.getStyleClass().addAll("card");
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(280);
        card.setPrefHeight(200);
        card.setStyle("-fx-border-style: dashed; -fx-cursor: hand;");
        card.setOnMouseClicked(e -> {
            onNewProject.run();
        });
        return card;
    }

    private void showOpenWithModal(Text text) {
        LocalizationService i18n = LocalizationService.getInstance();
        Label title = new Label(i18n.get("library.openProject"));
        title.getStyleClass().add("heading-lg");

        Label subtitle = new Label(titleText(text));
        subtitle.getStyleClass().add("text-muted");

        Button analysisBtn = new Button(i18n.get("library.openInTextAnalysis"));
        analysisBtn.getStyleClass().addAll("button", "primary");
        analysisBtn.setMaxWidth(Double.MAX_VALUE);
        analysisBtn.setOnAction(e -> {
            modal.hide();
            onOpenInAnalysis.accept(text);
        });

        Button compareBtn = new Button(i18n.get("library.compareWithFile"));
        compareBtn.getStyleClass().addAll("button", "secondary");
        compareBtn.setMaxWidth(Double.MAX_VALUE);
        compareBtn.setOnAction(e -> {
            showComparisonTextModal(text);
        });

        VBox content = new VBox(16, new VBox(4, title, subtitle), new VBox(10, analysisBtn, compareBtn));
        content.getStyleClass().add("modal-card");
        content.setPadding(new Insets(24));
        content.setPrefWidth(340);

        modal.show(content);
    }

    public void showComparisonTextModal(Text firstText) {
        comparisonTextModal.setTexts(firstText, texts);
        modal.show(comparisonTextModal);
    }

    private String titleText(Text text) {
        if (text.getTitle() != null && !text.getTitle().isBlank()) {
            return text.getTitle();
        }
        return text.getFileName();
    }

    private boolean matchesSearch(String... values) {
        String searchText = searchInput.getText().trim().toLowerCase();

        if (searchText.isEmpty()) {
            return true;
        }

        for (String value : values) {
            if (value != null && value.toLowerCase().contains(searchText)) {
                return true;
            }
        }

        return false;
    }
}
