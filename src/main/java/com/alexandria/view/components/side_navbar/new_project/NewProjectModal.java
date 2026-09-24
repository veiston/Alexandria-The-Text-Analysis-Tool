package com.alexandria.view.components.side_navbar.new_project;

import java.io.File;
import java.util.function.Consumer;

import com.alexandria.view.components.shared.LoadingIndicator;
import com.alexandria.view.components.shared.toggle.Toggle;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class NewProjectModal extends VBox {

    public enum SourceType {
        UPLOAD, PASTE
    }

    public enum Destination {
        ANALYSE, COMPARE
    }

    public record CreatedProject(
            String title,
            SourceType sourceType,
            String fileName,
            String textContent,
            File file,
            Destination destination) {
    }

    private final Toggle sourceToggle;
    private final Toggle uploadDestinationToggle;
    private final Toggle pasteDestinationToggle;

    private final VBox formHost;

    private final UploadPdfForm uploadForm;
    private final PasteTextForm pasteForm;

    private final LoadingIndicator loadingIndicator;

    private Consumer<CreatedProject> onCreated = project -> {
    };

    public NewProjectModal() {
        getStyleClass().add("modal-card");
        setSpacing(20);
        setPadding(new Insets(24));
        setPrefWidth(440);

        Label heading = new Label("New Project");
        heading.getStyleClass().add("heading-lg");

        sourceToggle = new Toggle("Upload File", "Paste Text");

        uploadDestinationToggle = createDestinationToggle();
        pasteDestinationToggle = createDestinationToggle();

        formHost = new VBox();

        uploadForm = new UploadPdfForm(
                createDestinationBox(uploadDestinationToggle));

        pasteForm = new PasteTextForm(
                createDestinationBox(pasteDestinationToggle));

        loadingIndicator = new LoadingIndicator();
        loadingIndicator.setMaxSize(28, 28);

        configureForms();
        configureSourceToggle();
        configureDestinationToggles();

        StackPane formStack = new StackPane(
                formHost,
                loadingIndicator);

        StackPane.setAlignment(loadingIndicator, Pos.CENTER);

        formHost.getChildren().add(uploadForm);

        getChildren().addAll(
                heading,
                sourceToggle,
                formStack);
    }

    private Toggle createDestinationToggle() {
        return new Toggle("Analyse", "Compare");
    }

    private VBox createDestinationBox(Toggle destinationToggle) {
        Label destinationLabel = new Label("Open In");
        destinationLabel.getStyleClass().add("form-label");

        VBox destinationBox = new VBox(8);
        destinationBox.getChildren().addAll(
                destinationLabel,
                destinationToggle);

        return destinationBox;
    }

    private void configureForms() {
        uploadForm.setOnSubmit(data -> submit(
                SourceType.UPLOAD,
                data.title(),
                data.fileName(),
                null,
                data.file()));

        pasteForm.setOnSubmit(data -> submit(
                SourceType.PASTE,
                data.title(),
                data.fileName(),
                data.content(),
                null));
    }

    private void configureSourceToggle() {
        sourceToggle.setOnToggle(index -> {
            if (index == 0) {
                showUpload();
            } else {
                showPaste();
            }
        });
    }

    private void configureDestinationToggles() {
        uploadDestinationToggle.setOnToggle(index ->
                pasteDestinationToggle.setSelectedIndex(index));

        pasteDestinationToggle.setOnToggle(index ->
                uploadDestinationToggle.setSelectedIndex(index));
    }

    private void showUpload() {
        uploadForm.reset();
        formHost.getChildren().setAll(uploadForm);
    }

    private void showPaste() {
        pasteForm.reset();
        formHost.getChildren().setAll(pasteForm);
    }

    private void submit(
            SourceType sourceType,
            String title,
            String fileName,
            String textContent,
            File file) {

        Destination destination =
                uploadDestinationToggle.getSelectedIndex() == 0
                        ? Destination.ANALYSE
                        : Destination.COMPARE;

        CreatedProject project = new CreatedProject(
                title,
                sourceType,
                fileName,
                textContent,
                file,
                destination);

        onCreated.accept(project);
    }

    public void setOnCreated(Consumer<CreatedProject> handler) {
        this.onCreated = handler;
    }

    public void setLoading(boolean loading) {
        formHost.setDisable(loading);
        sourceToggle.setDisable(loading);
        uploadDestinationToggle.setDisable(loading);
        pasteDestinationToggle.setDisable(loading);

        loadingIndicator.setLoading(loading);
    }

    public void showError(String message) {
        if (sourceToggle.getSelectedIndex() == 0) {
            uploadForm.showError(message);
        } else {
            pasteForm.showError(message);
        }
    }

    public void reset() {
        uploadForm.reset();
        pasteForm.reset();

        sourceToggle.setSelectedIndex(0);
        uploadDestinationToggle.setSelectedIndex(0);
        pasteDestinationToggle.setSelectedIndex(0);

        showUpload();
        setLoading(false);
    }

    public UploadPdfForm getUploadForm() {
        return uploadForm;
    }

    public PasteTextForm getPasteForm() {
        return pasteForm;
    }
}
