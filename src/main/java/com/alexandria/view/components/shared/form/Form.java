package com.alexandria.view.components.shared.form;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class Form extends VBox {

    private final List<FormField> fields;
    private final FormRenderer renderer;
    private final FormValidator validator;
    private final Label errorLabel;
    private final Button submitButton;

    private Consumer<Map<String, String>> onSubmit = values -> {};

    private Form(Builder builder) {
        this.fields = new ArrayList<>(builder.fields);
        this.renderer = new FormRenderer();
        this.validator = new FormValidator(builder.customValidator);

        getStyleClass().add("form");
        setSpacing(14);

        renderer.render(this, fields);

        errorLabel = createErrorLabel();
        getChildren().add(errorLabel);

        submitButton = createSubmitButton(builder.submitLabel);
        getChildren().add(submitButton);
    }

    private Label createErrorLabel() {
        Label label = new Label();
        label.getStyleClass().add("form-error");
        label.setVisible(false);
        label.setManaged(false);
        return label;
    }

    private Button createSubmitButton(String label) {
        Button button = new Button(label);
        button.getStyleClass().addAll("button", "primary");
        button.setMaxWidth(Double.MAX_VALUE);
        button.setOnAction(event -> handleSubmit());
        return button;
    }

    private void handleSubmit() {
        String error = validator.validate(fields, renderer);
        if (error != null) {
            showError(error);
            return;
        }
        clearError();
        onSubmit.accept(renderer.getValues());
    }

    public Map<String, String> getValues() { return renderer.getValues(); }
    public File getFile(String key) { return renderer.getFile(key); }
    public void setValue(String key, String value) { renderer.setValue(key, value); }

    public void setOnSubmit(Consumer<Map<String, String>> handler) {
        this.onSubmit = handler == null ? values -> {} : handler;
    }

    public void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    public void clearError() {
        errorLabel.setText("");
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
    }

    public void reset() {
        renderer.reset();
        clearError();
    }

    public static class Builder {

        private final List<FormField> fields = new ArrayList<>();
        private String submitLabel = "Submit";
        private Function<Map<String, String>, String> customValidator;

        public Builder field(String key, String label, FormField.Type type, boolean required) {
            fields.add(new FormField(key, label, type, required, List.of()));
            return this;
        }

        public Builder field(String key, String label, FormField.Type type, boolean required, FieldValidator... validators) {
            fields.add(new FormField(key, label, type, required,List.of(validators)));
            return this;
        }

        public Builder submitLabel(String label) {
            if (label == null || label.isBlank()) {
                throw new IllegalArgumentException("Submit label must not be blank.");
            }

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
