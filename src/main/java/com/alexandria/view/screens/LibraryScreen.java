package com.alexandria.view.screens;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Consumer;

import org.kordamp.ikonli.javafx.FontIcon;

import com.alexandria.model.Text;
import com.alexandria.view.components.shared.Card;
import com.alexandria.view.components.shared.EmptyState;
import com.alexandria.view.components.shared.SearchInput;
import com.alexandria.view.components.shared.modal.ConfirmationAlert;
import com.alexandria.view.components.shared.modal.Modal;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class LibraryScreen extends StackPane {

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("d.MM.uuuu");

    private final SearchInput searchInput = new SearchInput("Search library...");
    private final FlowPane cardsPane = new FlowPane(16, 16);
    private final StackPane contentArea = new StackPane();
    private final BorderPane libraryLayout = new BorderPane();
    private final VBox libraryBody = buildBody();
    private final Modal modal = new Modal();

    private List<Text> texts = List.of();

    private Consumer<Text> onOpenInAnalysis = text -> {};
    private Consumer<Text> onCompare = text -> {};
    private Consumer<Integer> onDeleteText = id -> {};
    private Runnable onNewProject = () -> {};
    private Runnable onShown = () -> {};

    public LibraryScreen() {
        getStyleClass().add("content-screen");
        searchInput.setMaxWidth(Double.MAX_VALUE);

        libraryLayout.setTop(buildHeader());
        libraryLayout.setCenter(libraryBody);
        libraryLayout.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        getChildren().addAll(libraryLayout, modal);
        StackPane.setAlignment(libraryLayout, Pos.CENTER);
        StackPane.setAlignment(modal, Pos.CENTER);
        modal.prefWidthProperty().bind(widthProperty());
        modal.prefHeightProperty().bind(heightProperty());

        configureActions();
        showTexts();

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

    private VBox buildHeader() {
        Label title = new Label("Library");
        title.getStyleClass().add("heading-xl");

        Label subtitle = new Label("Saved texts and documents");
        subtitle.getStyleClass().addAll("text-muted", "archive-subtitle");

        VBox header = new VBox(6, title, subtitle);
        header.getStyleClass().add("content-screen-header");
        return header;
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

    public void setTexts(List<Text> texts) {
        this.texts = List.of();
        if (texts != null) {
            this.texts = List.copyOf(texts);
        }
        showTexts();
    }

    public void setOnOpenInAnalysis(Consumer<Text> handler) {
        if (handler != null) {
            onOpenInAnalysis = handler;
        }
    }

    public void setOnCompare(Consumer<Text> handler) {
        if (handler != null) {
            onCompare = handler;
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
        EmptyState emptyState = new EmptyState(
                "Sign in to view your library",
                "Create an account to save and organize your texts and datasets here");
        libraryLayout.setCenter(emptyState);
        BorderPane.setAlignment(emptyState, Pos.CENTER);
    }

    private void showTexts() {
        libraryLayout.setCenter(libraryBody);
        cardsPane.getChildren().clear();

        boolean searching = !searchInput.getText().isBlank();

        if (!searching) {
            cardsPane.getChildren().add(createNewProjectCard());
        }

        for (Text text : texts) {
            String fileType = null;
            if (text.getFileType() != null) {
                fileType = text.getFileType().name();
            }

            if (matchesSearch(text.getTitle(), text.getFileName(), fileType)) {
                cardsPane.getChildren().add(createProjectCard(text));
            }
        }

        StackPane libraryContent = new StackPane();
        libraryContent.getStyleClass().add("archive-content");

        if (cardsPane.getChildren().isEmpty()) {
            EmptyState empty = new EmptyState(emptyMessage(), emptySubtitle());
            libraryContent.getChildren().add(empty);
            StackPane.setAlignment(empty, Pos.CENTER);
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
        if (!searchInput.getText().isBlank()) {
            return "Nothing found";
        }
        return "No saved texts yet";
    }

    private String emptySubtitle() {
        if (!searchInput.getText().isBlank()) {
            return "Try changing your search query";
        }
        return "Create a new project to add a text to your library";
    }

    private Card createProjectCard(Text text) {
        Card card = new Card(titleText(text));

        if (text.getFileType() != null) {
            card.setTypeText(text.getFileType().name());
        }
        card.setSourceText(text.getFileName());

        if (text.getCreatedAt() != null) {
            card.setFooterText("Added " + DATE_FORMAT.format(text.getCreatedAt()));
        }

        card.setActionText("Open with");
        card.getActionButton().setOnAction(e -> {
            showOpenWithModal(text);
        });

        card.getDeleteButton().setOnAction(e -> {
            if (ConfirmationAlert.show(
                    "Delete Text",
                    "Delete this text?",
                    "This action cannot be undone!")) {
                onDeleteText.accept(text.getId());
            }
        });

        return card;
    }

    private VBox createNewProjectCard() {
        FontIcon addIcon = new FontIcon("fas-plus-circle");
        addIcon.setIconSize(24);
        addIcon.getStyleClass().add("text-muted");

        Label title = new Label("Start New Analysis");
        title.getStyleClass().add("heading-md");

        Label subtitle = new Label("From library documents");
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
        Label title = new Label("Open Project");
        title.getStyleClass().add("heading-lg");

        Label subtitle = new Label(titleText(text));
        subtitle.getStyleClass().add("text-muted");

        Button analysisBtn = new Button("Open in Text Analysis");
        analysisBtn.getStyleClass().addAll("button", "primary");
        analysisBtn.setMaxWidth(Double.MAX_VALUE);
        analysisBtn.setOnAction(e -> {
            modal.hide();
            onOpenInAnalysis.accept(text);
        });

        Button compareBtn = new Button("Compare with another file...");
        compareBtn.getStyleClass().addAll("button", "secondary");
        compareBtn.setMaxWidth(Double.MAX_VALUE);
        compareBtn.setOnAction(e -> {
            modal.hide();
            onCompare.accept(text);
        });

        VBox content = new VBox(16, new VBox(4, title, subtitle), new VBox(10, analysisBtn, compareBtn));
        content.getStyleClass().add("modal-card");
        content.setPadding(new Insets(24));
        content.setPrefWidth(340);

        modal.show(content);
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
