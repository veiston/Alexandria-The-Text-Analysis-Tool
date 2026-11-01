package com.alexandria.view.components.side_navbar.new_project;

import java.util.function.Consumer;

import com.alexandria.service.LocalizationService;
import com.alexandria.view.components.shared.form.Form;
import com.alexandria.view.components.shared.form.FormField;

import javafx.scene.Node;
import javafx.scene.layout.VBox;

public class PasteTextForm extends VBox {

    public record PasteData(String title, String fileName, String content) {
    }

    private final Form form;

    private Consumer<PasteData> onSubmit = data -> {
    };

    public PasteTextForm(Node customContent) {
        getStyleClass().add("paste-text-form");

        LocalizationService i18n = LocalizationService.getInstance();
        form = new Form.Builder()
                .field("title", i18n.get("newProject.projectName"), FormField.Type.TEXT, true)
                .field("fileName", i18n.get("newProject.fileName"), FormField.Type.TEXT, true)
                .field("content", i18n.get("newProject.text"), FormField.Type.TEXT_AREA, true)
                .submitLabel(i18n.get("newProject.createProject"))
                .build();

        form.addCustomContent(customContent);

        form.setOnSubmit(values -> onSubmit.accept(new PasteData(
                values.get("title"),
                values.get("fileName"),
                values.get("content"))));

        getChildren().add(form);

        LocalizationService.getInstance().addListener(locale -> refreshText());
    }

    public void refreshText() {
        LocalizationService i18n = LocalizationService.getInstance();
        form.setFieldLabel("title", i18n.get("newProject.projectName"));
        form.setFieldLabel("fileName", i18n.get("newProject.fileName"));
        form.setFieldLabel("content", i18n.get("newProject.text"));
        form.setSubmitLabel(i18n.get("newProject.createProject"));
    }

    public void setOnSubmit(Consumer<PasteData> handler) {
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
