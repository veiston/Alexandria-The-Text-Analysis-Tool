package com.alexandria.view.components.side_navbar;

import java.util.function.Consumer;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

public class SideNavbar extends VBox {

    private final Label titleLabel;
    private final Label subtitleLabel;
    private final Button newProjectButton;

    private final SideNavbarNavigation navigation;

    private Runnable onNewProject = () -> {
    };

    public SideNavbar() {
        this("Alexandria Text Analysis", "Statistics Intelligence.");
    }

    public SideNavbar(String title, String subtitle) {
        getStyleClass().add("side-navbar");

        setPrefWidth(240);
        setMinWidth(240);
        setMaxWidth(240);

        setPadding(new Insets(20, 12, 16, 12));
        setSpacing(4);

        titleLabel = new Label(title);
        titleLabel.getStyleClass().add("heading-lg");
        titleLabel.setWrapText(true);

        subtitleLabel = new Label(subtitle);
        subtitleLabel.getStyleClass().add("text-muted");
        subtitleLabel.setWrapText(true);

        navigation = new SideNavbarNavigation();

        newProjectButton = buildNewProjectButton();

        getChildren().addAll(
                buildBrandSection(),
                newProjectButton,
                navigation.getMainNavigation(),
                buildSpacer(),
                buildSeparator(),
                navigation.getFooterNavigation());
    }

    private VBox buildBrandSection() {
        Image logoImage = new Image(getClass().getResourceAsStream("/images/logo.png"));
        ImageView logo = new ImageView(logoImage);
        logo.setFitWidth(36);
        logo.setFitHeight(36);
        logo.setPreserveRatio(true);
        logo.setSmooth(true);

        Rectangle clip = new Rectangle(36, 36);
        clip.setArcWidth(12);
        clip.setArcHeight(12);
        logo.setClip(clip);

        VBox titleBox = new VBox(titleLabel, subtitleLabel);
        titleBox.setAlignment(Pos.CENTER_LEFT);

        HBox row = new HBox(10, logo, titleBox);
        row.setAlignment(Pos.CENTER_LEFT);

        VBox brand = new VBox(row);
        brand.setPadding(new Insets(0, 8, 16, 8));

        return brand;
    }

    private Button buildNewProjectButton() {
        Button button = new Button("+ New Project");
        button.getStyleClass().addAll("button", "primary");
        button.setMaxWidth(Double.MAX_VALUE);
        button.setOnAction(e -> onNewProject.run());
        VBox.setMargin(button, new Insets(0, 8, 16, 8));

        return button;
    }

    private Region buildSpacer() {
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        return spacer;
    }

    private Separator buildSeparator() {
        Separator separator = new Separator();
        separator.getStyleClass().add("separator");

        return separator;
    }

    public void selectItem(String id) {
        navigation.selectItem(id);
    }

    public void setOnNavigate(Consumer<String> handler) {
        navigation.setOnNavigate(handler);
    }

    public void setOnFooterNavigate(Consumer<String> handler) {
        navigation.setOnFooterNavigate(handler);
    }

    public void setOnNewProject(Runnable action) {
        this.onNewProject = action;
    }

    public void setTitle(String title) {
        titleLabel.setText(title);
    }

    public void setSubtitle(String subtitle) {
        subtitleLabel.setText(subtitle);
    }

    public SideNavbarNavigation getNavigation() {
        return navigation;
    }
}
