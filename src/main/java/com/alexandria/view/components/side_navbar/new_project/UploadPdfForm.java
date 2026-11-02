package com.alexandria.view.components.side_navbar.new_project;

import java.io.File;
import java.util.function.Consumer;

import com.alexandria.service.LocalizationService;
import com.alexandria.view.components.shared.form.Form;
import com.alexandria.view.components.shared.form.FormField;

import javafx.scene.Node;
import javafx.scene.layout.VBox;

public class UploadPdfForm extends VBox {

    public record UploadData(String title, String fileName, File file) {
    }

    private final Form form;

    private Consumer<UploadData> onSubmit = data -> {
    };

    public UploadPdfForm(Node customContent) {
        getStyleClass().add("upload-pdf-form");

        form = new Form.Builder()
                .field("title", "newProject.projectName", FormField.Type.TEXT, true)
                .field("fileName", "newProject.fileNameOptional", FormField.Type.TEXT, false)
                .field("file", "newProject.document", FormField.Type.DOCUMENT_FILE, true)
                .submitLabel("newProject.createProject")
                .build();

        form.addCustomContent(customContent);

        form.setOnSubmit(values -> onSubmit.accept(new UploadData(
                values.get("title"),
                values.get("fileName"),
                form.getFile("file"))));

        getChildren().add(form);
    }

    public void refreshText() {
        form.refreshText();
    }

    public void setOnSubmit(Consumer<UploadData> handler) {
        this.onSubmit = handler;
    }

    public void showError(String message) {
        form.showError(message);
    }

    public void reset() {
        form.reset();
    }

    public Form getForm() {
        return form;
    }
}
