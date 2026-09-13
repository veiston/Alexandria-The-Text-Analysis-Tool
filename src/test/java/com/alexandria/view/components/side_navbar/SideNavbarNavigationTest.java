package com.alexandria.view.components.side_navbar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import javafx.application.Platform;
import javafx.scene.control.ToggleButton;

public class SideNavbarNavigationTest {

    @BeforeClass
    public static void startJavaFx() {
        try {
            Platform.startup(() -> {
            });
        } catch (IllegalStateException ignored) {
        }
    }

    @Test
    public void navigationContainsMainAndFooterItems() {
        SideNavbarNavigation navigation = new SideNavbarNavigation();

        assertEquals(4, navigation.getMainNavigation().getChildren().size());
        assertEquals(3, navigation.getFooterNavigation().getChildren().size());
    }

    @Test
    public void selectItemSelectsLibrary() {
        SideNavbarNavigation navigation = new SideNavbarNavigation();

        navigation.selectItem("library");

        ToggleButton libraryButton = (ToggleButton) navigation.getMainNavigation().getChildren().get(0);
        assertTrue(libraryButton.isSelected());
    }

    @Test
    public void selectItemSelectsProfile() {
        SideNavbarNavigation navigation = new SideNavbarNavigation();

        navigation.selectItem("profile");

        ToggleButton profileButton = (ToggleButton) navigation.getFooterNavigation().getChildren().get(2);
        assertTrue(profileButton.isSelected());
    }
}
