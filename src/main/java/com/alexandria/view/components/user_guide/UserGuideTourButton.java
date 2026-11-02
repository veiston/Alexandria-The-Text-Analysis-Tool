package com.alexandria.view.components.user_guide;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import com.alexandria.service.LocalizationService;
import com.alexandria.view.components.shared.tour.Tour;

import javafx.event.Event;
import javafx.scene.control.Button;

public class UserGuideTourButton extends Button {

    public UserGuideTourButton() {
        super(LocalizationService.getInstance().get("userGuide.startTour"));

        FontIcon playIcon = new FontIcon(FontAwesomeSolid.PLAY);
        playIcon.setIconSize(16);
        playIcon.getStyleClass().add("user-guide-tour-icon");
        setGraphic(playIcon);
        setGraphicTextGap(8);
        getStyleClass().addAll("button", "primary", "user-guide-tour-button");
        setOnAction(event -> fireEvent(new Event(Tour.START_EVENT)));
    }

    public void refreshText() {
        setText(LocalizationService.getInstance().get("userGuide.startTour"));
    }
}
