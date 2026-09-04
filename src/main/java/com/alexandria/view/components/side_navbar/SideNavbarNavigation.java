package com.alexandria.view.components.side_navbar;

import java.util.List;
import java.util.function.Consumer;

import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import javafx.geometry.Pos;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

public class SideNavbarNavigation {

    private final ToggleGroup navGroup = new ToggleGroup();

    private final VBox mainNavigation;
    private final VBox footerNavigation;

    private Consumer<String> onNavigate = id -> {
    };
    private Consumer<String> onFooterNavigate = id -> {
    };

    public SideNavbarNavigation() {
        mainNavigation = buildMainNavigation();
        footerNavigation = buildFooterNavigation();
    }

    private VBox buildMainNavigation() {
        return buildNavGroup(List.of(
                new NavItem("library", "Library", FontAwesomeSolid.FOLDER),
                new NavItem("analyze", "Analyze", FontAwesomeSolid.CHART_BAR),
                new NavItem("compare", "Compare", FontAwesomeSolid.BALANCE_SCALE),
                new NavItem("archive", "Archive", FontAwesomeSolid.ARCHIVE)),
                false);
    }

    private VBox buildFooterNavigation() {
        return buildNavGroup(List.of(
                new NavItem("settings", "Settings", FontAwesomeSolid.COG),
                new NavItem("userguide", "User Guide", FontAwesomeSolid.QUESTION_CIRCLE),
                new NavItem("profile", "Profile", FontAwesomeSolid.USER)),
                true);
    }

    private VBox buildNavGroup(List<NavItem> items, boolean footer) {
        VBox container = new VBox(2);
        for (NavItem item : items) {
            ToggleButton button = createNavButton(item, footer);
            container.getChildren().add(button);
        }

        return container;
    }

    private ToggleButton createNavButton(NavItem item, boolean footer) {
        ToggleButton button = new ToggleButton(item.label());

        FontIcon icon = new FontIcon(item.icon());
        icon.setIconSize(15);
        icon.getStyleClass().add("nav-item-icon");

        button.setGraphic(icon);
        button.setUserData(item.id());
        button.setToggleGroup(navGroup);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setAlignment(Pos.CENTER_LEFT);
        button.getStyleClass().add("nav-item");
        button.setOnAction(e -> {
            if (!button.isSelected()) {
                return;
            }

            if (footer) {
                onFooterNavigate.accept(item.id());
            } else {
                onNavigate.accept(item.id());
            }
        });

        return button;
    }

    public VBox getMainNavigation() {
        return mainNavigation;
    }

    public VBox getFooterNavigation() {
        return footerNavigation;
    }

    public void selectItem(String id) {
        for (var toggle : navGroup.getToggles()) {
            if (toggle instanceof ToggleButton button && id.equals(button.getUserData())) {
                button.setSelected(true);
                break;
            }
        }
    }

    public void setOnNavigate(Consumer<String> handler) {
        this.onNavigate = handler;
    }

    public void setOnFooterNavigate(Consumer<String> handler) {
        this.onFooterNavigate = handler;
    }

    private record NavItem(
            String id,
            String label,
            Ikon icon) {
    }
}
