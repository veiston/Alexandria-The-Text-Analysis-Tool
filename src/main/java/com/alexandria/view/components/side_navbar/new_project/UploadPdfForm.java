package com.alexandria.view.components.side_navbar.new_project;

import java.io.File;
import java.util.function.Consumer;

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
                .field("title", "Project Name", FormField.Type.TEXT, true)
                .field("fileName", "File Name (optional)", FormField.Type.TEXT, false)
                .field("file", "Document", FormField.Type.DOCUMENT_FILE, true)
                .submitLabel("Create Project")
                .build();

        form.addCustomContent(customContent);

        form.setOnSubmit(values -> onSubmit.accept(new UploadData(
                values.get("title"),
                values.get("fileName"),
                form.getFile("file"))));

        getChildren().add(form);
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
}
