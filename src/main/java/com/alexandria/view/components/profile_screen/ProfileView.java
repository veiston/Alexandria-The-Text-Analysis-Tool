package com.alexandria.view.components.profile_screen;

import com.alexandria.model.User;

import javafx.geometry.Rectangle2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

public class ProfileView extends VBox {
    private final ImageView avatar;
    private final Label nameLabel;
    private final Label emailLabel;
    private final Label organizationLabel;

    private final Button editButton;
    private final Button changePasswordButton;
    private final Button logoutButton;
    private final Button deleteAccountButton;

    private Runnable onEdit = () -> {
    };
    private Runnable onChangePassword = () -> {
    };
    private Runnable onLogout = () -> {
    };
    private Runnable onDeleteAccount = () -> {
    };

    public ProfileView() {
        getStyleClass().add("profile-view");
        setSpacing(24);

        avatar = new ImageView();
        avatar.setFitWidth(72);
        avatar.setFitHeight(72);
        avatar.setPreserveRatio(false);
        avatar.setClip(new Circle(36, 36, 36));
        avatar.getStyleClass().add("profile-avatar");

        nameLabel = new Label();
        nameLabel.getStyleClass().add("heading-lg");

        emailLabel = new Label();
        emailLabel.getStyleClass().add("text-muted");

        organizationLabel = new Label();
        organizationLabel.getStyleClass().add("text-muted");

        VBox profileInfo = new VBox(4, nameLabel, emailLabel, organizationLabel);
        profileInfo.getStyleClass().add("profile-info");

        editButton = new Button("Edit Profile");
        editButton.getStyleClass().addAll("button", "secondary", "profile-edit-button");
        editButton.setOnAction(e -> onEdit.run());

        VBox profileData = new VBox(profileInfo);
        HBox.setHgrow(profileData, Priority.ALWAYS);

        HBox profileHeader = new HBox(16, avatar, profileData, editButton);
        profileHeader.setAlignment(Pos.CENTER_LEFT);

        VBox profileCard = new VBox(profileHeader);
        profileCard.getStyleClass().add("profile-card");

        changePasswordButton = new Button("Change Password");
        changePasswordButton.getStyleClass().addAll("button", "secondary", "settings-action-button");
        changePasswordButton.setOnAction(e -> onChangePassword.run());

        VBox securitySection = createSection("Security",
                createSettingRow(
                        "Password",
                        changePasswordButton));

        logoutButton = new Button("Log Out");
        logoutButton.getStyleClass().addAll("button", "danger", "danger-action-button");
        logoutButton.setOnAction(e -> onLogout.run());

        deleteAccountButton = new Button("Delete Account");
        deleteAccountButton.getStyleClass().addAll("button", "danger", "danger-action-button");
        deleteAccountButton.setOnAction(e -> onDeleteAccount.run());

        VBox dangerActions = new VBox(8, logoutButton, deleteAccountButton);
        dangerActions.setAlignment(Pos.CENTER_RIGHT);
        dangerActions.getStyleClass().add("danger-actions");

        VBox dangerSection = createSection("Danger Zone", dangerActions);

        VBox settingsList = new VBox(securitySection, dangerSection);
        settingsList.getStyleClass().add("settings-list");
        getChildren().addAll(profileCard, settingsList);
    }

    private VBox createSection(String title, Node... content) {

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("settings-section-title");

        VBox section = new VBox();
        section.getStyleClass().add("settings-section");
        section.getChildren().add(titleLabel);
        section.getChildren().addAll(content);

        return section;
    }

    private HBox createSettingRow(String name, Node control) {

        Label nameLabel = new Label(name);
        nameLabel.getStyleClass().add("settings-name");

        HBox.setHgrow(nameLabel, Priority.ALWAYS);

        HBox row = new HBox(nameLabel, control);
        row.setAlignment(Pos.CENTER_LEFT);
        row.getStyleClass().add("settings-row");

        return row;
    }

    public void setUser(User user) {
        nameLabel.setText(user.getName());
        emailLabel.setText(user.getEmail());

        organizationLabel.setText(
                user.getOrganization() == null || user.getOrganization().isBlank()
                        ? "No organization set"
                        : user.getOrganization());

        if (user.getPhoto() != null && !user.getPhoto().isBlank()) {
            applyCoverImage(new Image("file:" + user.getPhoto(), true));
        } else {
            applyCoverImage(new Image(getClass().getResourceAsStream("/images/default-avatar.png")));
        }
    }

    /* Crop the image */

    private void applyCoverImage(Image image) {
        if (image.getWidth() > 0 && image.getHeight() > 0) {
            setCoverViewport(image);
        } else {
            image.widthProperty().addListener((obs, oldVal, newVal) -> {
                if (image.getWidth() > 0 && image.getHeight() > 0) {
                    setCoverViewport(image);
                }
            });
        }
        avatar.setImage(image);
    }

    private void setCoverViewport(Image image) {
        double imgWidth = image.getWidth();
        double imgHeight = image.getHeight();
        double size = Math.min(imgWidth, imgHeight);
        double x = (imgWidth - size) / 2;
        double y = (imgHeight - size) / 2;

        avatar.setViewport(new Rectangle2D(x, y, size, size));
    }

    public void setOnEdit(Runnable action) {
        this.onEdit = action;
    }

    public void setOnChangePassword(Runnable action) {
        this.onChangePassword = action;
    }

    public void setOnLogout(Runnable action) {
        this.onLogout = action;
    }

    public void setOnDeleteAccount(Runnable action) {
        this.onDeleteAccount = action;
    }
}
