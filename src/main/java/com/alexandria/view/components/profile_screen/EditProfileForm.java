package com.alexandria.view.components.profile_screen;

import java.util.Map;
import java.util.function.Consumer;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import com.alexandria.model.User;
import com.alexandria.service.LocalizationService;
import com.alexandria.view.components.shared.form.Form;
import com.alexandria.view.components.shared.form.FormField;
import com.alexandria.view.components.shared.form.validation.EmailValidator;
import com.alexandria.view.components.shared.form.validation.FileValidator;
import com.alexandria.view.components.shared.form.validation.TextValidator;

public class EditProfileForm extends VBox {
    private static final double MAX_WIDTH = 380;
    private final Label title = new Label();
    private final Form form;
    private Consumer<Map<String, String>> onSave = values -> {
    };

    public EditProfileForm() {
        getStyleClass().add("modal-card");
        setSpacing(20);
        setPadding(new Insets(24));
        setMaxWidth(MAX_WIDTH);
        setPrefWidth(420);

        title.getStyleClass().add("heading-lg");

        form = new Form.Builder()
                .field("name", "profile.username", FormField.Type.TEXT, false, new TextValidator())
                .field("email", "profile.email", FormField.Type.TEXT, false, new EmailValidator())
                .field("organization", "profile.organization", FormField.Type.TEXT, false, new TextValidator())
                .field("photo", "profile.photo", FormField.Type.IMG_FILE, false, new FileValidator())
                .submitLabel("profile.saveChanges")
                .customValidator(values -> {
                    boolean anyFilled = values.values().stream()
                            .anyMatch(v -> v != null && !v.isBlank());

                    return anyFilled ? null : LocalizationService.getInstance().get("vm.changeAtLeastOneField");
                })
                .build();

        form.setOnSubmit(values -> onSave.accept(values));
        getChildren().addAll(title, form);

        refreshText();
        LocalizationService.getInstance().addListener(locale -> refreshText());
    }

    public void refreshText() {
        title.setText(LocalizationService.getInstance().get("profile.edit"));
    }

    public void prefill(User user) {
        if (user == null)
            return;

        form.setValue("name", user.getName());
        form.setValue("email", user.getEmail());
        form.setValue("organization", user.getOrganization());
    }

    public void setOnSave(Consumer<Map<String, String>> handler) {
        this.onSave = handler;
    }

    public void showError(String message) {
        form.showError(message);
    }

    public void reset() {
        form.reset();
    }
}
