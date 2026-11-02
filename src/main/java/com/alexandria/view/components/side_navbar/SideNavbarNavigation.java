package com.alexandria.view.components.side_navbar;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import com.alexandria.service.LocalizationService;

import javafx.geometry.Pos;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

public class SideNavbarNavigation {

    private final ToggleGroup navGroup = new ToggleGroup();
    private final Map<String, ToggleButton> navButtons = new HashMap<>();

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
        LocalizationService i18n = LocalizationService.getInstance();
        return buildNavGroup(List.of(
                new NavItem("library", i18n.get("library"), FontAwesomeSolid.FOLDER),
                new NavItem("analyze", i18n.get("analyze"), FontAwesomeSolid.CHART_BAR),
                new NavItem("compare", i18n.get("compare"), FontAwesomeSolid.BALANCE_SCALE),
                new NavItem("archive", i18n.get("archive"), FontAwesomeSolid.ARCHIVE)),
                false);
    }

    private VBox buildFooterNavigation() {
        LocalizationService i18n = LocalizationService.getInstance();
        return buildNavGroup(List.of(
                new NavItem("settings", i18n.get("settings"), FontAwesomeSolid.COG),
                new NavItem("userguide", i18n.get("userGuide"), FontAwesomeSolid.QUESTION_CIRCLE),
                new NavItem("profile", i18n.get("profile"), FontAwesomeSolid.USER)),
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

        navButtons.put(item.id(), button);
        return button;
    }

    public void refreshText() {
        LocalizationService i18n = LocalizationService.getInstance();
        for (Map.Entry<String, ToggleButton> entry : navButtons.entrySet()) {
            String key = "userguide".equals(entry.getKey()) ? "userGuide" : entry.getKey();
            entry.getValue().setText(i18n.get(key));
        }
    }

    public VBox getMainNavigation() {
        return mainNavigation;
    }

    public VBox getFooterNavigation() {
        return footerNavigation;
    }

    public void selectItem(String id) {
        ToggleButton button = navButtons.get(id);
        if (button != null) {
            button.setSelected(true);
        }
    }

    public ToggleButton getButton(String id) {
        return navButtons.get(id);
    }

    public void setOnNavigate(Consumer<String> onNavigate) {
        this.onNavigate = onNavigate;
    }

    public void setOnFooterNavigate(Consumer<String> onFooterNavigate) {
        this.onFooterNavigate = onFooterNavigate;
    }

    private record NavItem(String id, String label, Ikon icon) {
    }
}
