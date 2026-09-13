package com.alexandria.view.components.profile_screen;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

import com.alexandria.model.User;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ProfileViewTest {

    @BeforeClass
    public static void startJavaFx() {
        try {
            Platform.startup(() -> {
            });
        } catch (IllegalStateException ignored) {
        }
    }

    @Test
    public void setUserShowsNameEmailAndOrganization() {
        ProfileView view = new ProfileView();
        User user = new User(
                1,
                "Existing",
                "taken@example.com",
                null,
                "OldOrg",
                "hash");

        view.setUser(user);

        VBox profileInfo = getProfileInfo(view);
        assertEquals("Existing", ((Label) profileInfo.getChildren().get(0)).getText());
        assertEquals("taken@example.com", ((Label) profileInfo.getChildren().get(1)).getText());
        assertEquals("OldOrg", ((Label) profileInfo.getChildren().get(2)).getText());
    }

    @Test
    public void setUserShowsDefaultOrganizationWhenItIsEmpty() {
        ProfileView view = new ProfileView();
        User user = new User(
                1,
                "Existing",
                "taken@example.com",
                null,
                "",
                "hash");

        view.setUser(user);

        VBox profileInfo = getProfileInfo(view);
        assertEquals("No organization set", ((Label) profileInfo.getChildren().get(2)).getText());
    }

    private VBox getProfileInfo(ProfileView view) {
        VBox profileCard = (VBox) view.getChildren().get(0);
        HBox profileHeader = (HBox) profileCard.getChildren().get(0);
        VBox profileData = (VBox) profileHeader.getChildren().get(1);
        return (VBox) profileData.getChildren().get(0);
    }
}
