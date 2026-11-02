package com.alexandria.view.components.shared.form;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import com.alexandria.service.LocalizationService;

public class Form extends VBox {

    private final List<FormField> fields;
    private final FormRenderer renderer;
    private final FormValidator validator;
    private final Label errorLabel;
    private final Button submitButton;
    private String submitLabel;

    private Consumer<Map<String, String>> onSubmit = values -> {
    };

    private Form(Builder builder) {
        this.fields = new ArrayList<>(builder.fields);
        this.submitLabel = builder.submitLabel;
        this.renderer = new FormRenderer();
        this.validator = new FormValidator(builder.customValidator);

        getStyleClass().add("form");
        setSpacing(14);

        renderer.render(this, fields);

        errorLabel = createErrorLabel();
        getChildren().add(errorLabel);

        submitButton = createSubmitButton(LocalizationService.getInstance().getOrDefault(submitLabel, submitLabel));
        getChildren().add(submitButton);

        LocalizationService.getInstance().addListener(locale -> refreshText());
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

    public Map<String, String> getValues() {
        return renderer.getValues();
    }

    public File getFile(String key) {
        return renderer.getFile(key);
    }

    public Node getFieldNode(String key) {
        return renderer.getFieldNode(key);
    }

    public Button getSubmitButton() {
        return submitButton;
    }

    public void setFieldLabel(String key, String label) {
        FormField field = fields.stream().filter(f -> f.key().equals(key)).findFirst().orElse(null);
        boolean required = field != null && field.required();
        renderer.setFieldLabel(key, LocalizationService.getInstance().getOrDefault(label, label), required);
    }

    public void setSubmitLabel(String label) {
        this.submitLabel = label;
        submitButton.setText(LocalizationService.getInstance().getOrDefault(label, label));
    }

    public void refreshText() {
        for (FormField field : fields) {
            renderer.setFieldLabel(field.key(), field.label(), field.required());
        }
        submitButton.setText(LocalizationService.getInstance().getOrDefault(submitLabel, submitLabel));
        renderer.refreshFileLabels();
    }

    public void refreshFileLabels() {
        renderer.refreshFileLabels();
    }

    public void setValue(String key, String value) {
        renderer.setValue(key, value);
    }

    public void setOnSubmit(Consumer<Map<String, String>> handler) {
        this.onSubmit = handler == null
                ? values -> {
                }
                : handler;
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

    public void addCustomContent(Node node) {
        if (node == null) {
            return;
        }

        int submitIndex = getChildren().indexOf(submitButton);

        if (submitIndex >= 0) {
            getChildren().add(submitIndex, node);
        }
    }

    public void reset() {
        renderer.reset();
        clearError();
    }

    public static class Builder {

        private final List<FormField> fields = new ArrayList<>();
        private String submitLabel = "Submit";
        private Function<Map<String, String>, String> customValidator;

        public Builder field(
                String key,
                String label,
                FormField.Type type,
                boolean required) {

            fields.add(new FormField(
                    key,
                    label,
                    type,
                    required,
                    List.of()));

            return this;
        }

        public Builder field(
                String key,
                String label,
                FormField.Type type,
                boolean required,
                FieldValidator... validators) {

            fields.add(new FormField(
                    key,
                    label,
                    type,
                    required,
                    List.of(validators)));

            return this;
        }

        public Builder submitLabel(String label) {
            if (label == null || label.isBlank()) {
                throw new IllegalArgumentException(
                        "Submit label must not be blank.");
            }

            this.submitLabel = label;
            return this;
        }

        public Builder customValidator(
                Function<Map<String, String>, String> validator) {

            this.customValidator = validator;
            return this;
        }

        public Form build() {
            return new Form(this);
        }
    }
}
