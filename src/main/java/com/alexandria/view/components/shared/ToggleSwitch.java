package com.alexandria.view.components.shared;

import javafx.scene.control.ToggleButton;

public class ToggleSwitch extends ToggleButton {

    public ToggleSwitch() {
        setText("●");
        setAccessibleText("Toggle setting");
        getStyleClass().add("toggle-switch");
    }
}
