package com.alexandria.view.components.shared.form;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

public final class FormRenderer {

    private final Map<String, TextInputControl> inputs = new LinkedHashMap<>();
    private final Map<String, File> files = new LinkedHashMap<>();

    public void render(VBox container, List<FormField> fields) {
        for (FormField field : fields) {
            container.getChildren().add(buildFieldRow(container, field));
        }
    }

    private VBox buildFieldRow(VBox container, FormField field) {
        Label label = new Label(field.label() + (field.required() ? " *" : ""));
        label.getStyleClass().add("form-label");

        VBox row = new VBox(6, label);
        row.getChildren().add(switch (field.type()) {
            case TEXT -> buildTextField(field);
            case PASSWORD -> buildPasswordField(field);
            case TEXT_AREA -> buildTextArea(field);
            case IMG_FILE, DOCUMENT_FILE -> buildFilePicker(container, field);
        });
        return row;
    }

    private TextField buildTextField(FormField field) {
        TextField input = new TextField();
        input.getStyleClass().add("form-input");
        inputs.put(field.key(), input);
        return input;
    }

    private PasswordField buildPasswordField(FormField field) {
        PasswordField input = new PasswordField();
        input.getStyleClass().add("form-input");
        inputs.put(field.key(), input);
        return input;
    }

    private TextArea buildTextArea(FormField field) {
        TextArea input = new TextArea();
        input.setWrapText(true);
        input.getStyleClass().add("form-input");
        inputs.put(field.key(), input);
        return input;
    }

    private HBox buildFilePicker(VBox container, FormField field) {
        Label fileLabel = new Label("No file selected");
        fileLabel.getStyleClass().add("file-name");


        Button chooseButton = new Button("Choose File");
        chooseButton.getStyleClass().addAll("button", "secondary");
        chooseButton.setOnAction(e -> chooseFile(container, field, fileLabel));

        return new HBox(10, chooseButton, fileLabel);
    }

    private void chooseFile(VBox container, FormField field, Label fileLabel) {
        if (container.getScene() == null || container.getScene().getWindow() == null) {
            return;
        }

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select " + field.label());

        switch (field.type()) {
            case IMG_FILE -> chooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter(
                            "Images", "*.png", "*.jpg", "*.jpeg"));

            case DOCUMENT_FILE -> chooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter(
                            "Documents", "*.pdf", "*.txt"));

            default -> {
                return;
            }
        }

        File selectedFile = chooser.showOpenDialog(
                container.getScene().getWindow());

        if (selectedFile == null) {
            return;
        }

        files.put(field.key(), selectedFile);
        fileLabel.setText(selectedFile.getName());

        fileLabel.getStyleClass().remove("file-name");
        fileLabel.getStyleClass().add("file-name-selected");
    }

    public Map<String, String> getValues() {
        Map<String, String> values = new LinkedHashMap<>();
        inputs.forEach((key, input) -> values.put(key, input.getText()));
        files.forEach((key, file) -> values.put(key, file.getAbsolutePath()));
        return values;
    }

    public String getValue(String key) {
        TextInputControl input = inputs.get(key);
        return input == null ? null : input.getText();
    }

    public String getValue(FormField field) {
        if (isFileField(field)) {
            File file = files.get(field.key());
            return file == null ? null : file.getAbsolutePath();
        }
        return getValue(field.key());
    }

    public File getFile(String key) {
        return files.get(key);
    }

    public boolean isEmpty(FormField field) {
        String value = getValue(field);
        return value == null || value.isBlank();
    }

    public void setValue(String key, String value) {
        TextInputControl input = inputs.get(key);
        if (input != null) {
            input.setText(value == null ? "" : value);
        }
    }

    public void reset() {
        inputs.values().forEach(TextInputControl::clear);
        files.clear();
    }

    private boolean isFileField(FormField field) {
        return switch (field.type()) {
            case IMG_FILE, DOCUMENT_FILE -> true;
            default -> false;
        };
    }
}
