package com.alexandria.view.components.side_navbar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import javafx.application.Platform;
import javafx.scene.control.ToggleButton;

public class SideNavbarTest {

    @BeforeClass
    public static void startJavaFx() {
        try {
            Platform.startup(() -> {
            });
        } catch (IllegalStateException ignored) {
        }
    }

    @Test
    public void sidebarContainsNavigation() {
        SideNavbar sidebar = new SideNavbar();

        assertEquals(6, sidebar.getChildren().size());
        assertNotNull(sidebar.getNavigation());
    }

    @Test
    public void selectItemSelectsLibrary() {
        SideNavbar sidebar = new SideNavbar();

        sidebar.selectItem("library");

        ToggleButton libraryButton = (ToggleButton) sidebar.getNavigation()
                .getMainNavigation().getChildren().get(0);
        assertTrue(libraryButton.isSelected());
    }

    @Test
    public void selectItemSelectsProfile() {
        SideNavbar sidebar = new SideNavbar();

        sidebar.selectItem("profile");

        ToggleButton profileButton = (ToggleButton) sidebar.getNavigation()
                .getFooterNavigation().getChildren().get(2);
        assertTrue(profileButton.isSelected());
    }
}
