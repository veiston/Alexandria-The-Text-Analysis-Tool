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

        LocalizationService i18n = LocalizationService.getInstance();
        form = new Form.Builder()
                .field("title", i18n.get("newProject.projectName"), FormField.Type.TEXT, true)
                .field("fileName", i18n.get("newProject.fileNameOptional"), FormField.Type.TEXT, false)
                .field("file", i18n.get("newProject.document"), FormField.Type.DOCUMENT_FILE, true)
                .submitLabel(i18n.get("newProject.createProject"))
                .build();

        form.addCustomContent(customContent);

        form.setOnSubmit(values -> onSubmit.accept(new UploadData(
                values.get("title"),
                values.get("fileName"),
                form.getFile("file"))));

        getChildren().add(form);

        LocalizationService.getInstance().addListener(locale -> refreshText());
    }

    public void refreshText() {
        LocalizationService i18n = LocalizationService.getInstance();
        form.setFieldLabel("title", i18n.get("newProject.projectName"));
        form.setFieldLabel("fileName", i18n.get("newProject.fileNameOptional"));
        form.setFieldLabel("file", i18n.get("newProject.document"));
        form.setSubmitLabel(i18n.get("newProject.createProject"));
        form.refreshFileLabels();
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
