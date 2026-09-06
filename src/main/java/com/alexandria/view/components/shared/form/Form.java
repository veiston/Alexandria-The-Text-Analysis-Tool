package com.alexandria.view.components.shared.form;

import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Reusable, declarative form built via Form.Builder.
 */
public class Form extends VBox {
    private final List<FormField> fields;
    private final Map<String, TextInputControl> inputs = new LinkedHashMap<>();
    private final Map<String, String> filePaths = new HashMap<>();
    private final Function<Map<String, String>, String> customValidator;
    private final Label errorLabel;
    private final Button submitButton;
    private Consumer<Map<String, String>> onSubmit = values -> {
    };

    private Form(Builder builder) {
        this.fields = builder.fields;
        this.customValidator = builder.customValidator;

        getStyleClass().add("form");
        setSpacing(14);

        errorLabel = new Label();
        errorLabel.getStyleClass().add("form-error");
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);

        for (FormField field : fields) {
            getChildren().add(buildFieldRow(field));
        }
        getChildren().add(errorLabel);

        submitButton = new Button(builder.submitLabel);
        submitButton.getStyleClass().addAll("button", "primary");
        submitButton.setMaxWidth(Double.MAX_VALUE);
        submitButton.setOnAction(e -> handleSubmit());
        getChildren().add(submitButton);
    }

    private VBox buildFieldRow(FormField field) {
        Label label = new Label(field.label() + (field.required() ? " *" : ""));
        label.getStyleClass().add("form-label");

        VBox row = new VBox(6, label);
        if (field.type() == FormField.Type.FILE) {
            row.getChildren().add(buildFilePicker(field));
        } else {
            TextInputControl input = field.type() == FormField.Type.PASSWORD
                    ? new PasswordField()
                    : new TextField();
            input.getStyleClass().add("form-input");
            inputs.put(field.key(), input);
            row.getChildren().add(input);
        }
        return row;
    }

    private HBox buildFilePicker(FormField field) {
        Label pathLabel = new Label("No file selected");
        pathLabel.getStyleClass().add("text-muted");

        Button chooseButton = new Button("Choose Photo");
        chooseButton.getStyleClass().addAll("button", "secondary");
        chooseButton.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Select " + field.label());
            chooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));
            File file = chooser.showOpenDialog(getScene().getWindow());
            if (file != null) {
                filePaths.put(field.key(), file.getAbsolutePath());
                pathLabel.setText(file.getName());
            }
        });

        return new HBox(10, chooseButton, pathLabel);
    }

    private void handleSubmit() {
        Map<String, String> values = getValues();

        for (FormField field : fields) {
            if (field.required() && field.type() != FormField.Type.FILE && isBlank(values.get(field.key()))) {
                showError(field.label() + " is required.");
                return;
            }
        }

        if (customValidator != null) {
            String error = customValidator.apply(values);
            if (error != null) {
                showError(error);
                return;
            }
        }

        clearError();
        onSubmit.accept(values);
    }

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    public Map<String, String> getValues() {
        Map<String, String> values = new LinkedHashMap<>();
        inputs.forEach((key, input) -> values.put(key, input.getText()));
        filePaths.forEach(values::put);
        return values;
    }

    /* Pre-fills a text-based field */
    public void setValue(String key, String value) {
        TextInputControl input = inputs.get(key);
        if (input != null)
            input.setText(value == null ? "" : value);
    }

    public void setOnSubmit(Consumer<Map<String, String>> handler) {
        this.onSubmit = handler;
    }

    public void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    public void clearError() {
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
    }

    public void reset() {
        inputs.values().forEach(TextInputControl::clear);
        filePaths.clear();
        clearError();
    }

    public static class Builder {
        private final List<FormField> fields = new ArrayList<>();
        private String submitLabel = "Submit";
        private Function<Map<String, String>, String> customValidator;

        public Builder field(String key, String label, FormField.Type type, boolean required) {
            fields.add(new FormField(key, label, type, required));
            return this;
        }

        public Builder submitLabel(String label) {
            this.submitLabel = label;
            return this;
        }

        public Builder customValidator(Function<Map<String, String>, String> validator) {
            this.customValidator = validator;
            return this;
        }

        public Form build() {
            return new Form(this);
        }
    }
}