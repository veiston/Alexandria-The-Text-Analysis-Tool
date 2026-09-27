package com.alexandria.view.components.shared.toggle;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javafx.geometry.Pos;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class Toggle extends HBox {
    private final ToggleGroup toggleGroup = new ToggleGroup();
    private final List<ToggleButton> buttons = new ArrayList<>();

    private Consumer<Integer> onToggle = index -> {
    };

    public Toggle(String... labels) {
        getStyleClass().add("toggle");
        setAlignment(Pos.CENTER);
        setSpacing(4);

        for (int i = 0; i < labels.length; i++) {
            createButton(labels[i], i);
        }

        if (!buttons.isEmpty()) {
            buttons.get(0).setSelected(true);
        }
    }

    private void createButton(String label, int index) {
        ToggleButton button = new ToggleButton(label);
        button.getStyleClass().add("toggle-option");
        button.setToggleGroup(toggleGroup);
        button.setMaxWidth(Double.MAX_VALUE);

        HBox.setHgrow(button, Priority.ALWAYS);
        button.setOnAction(event -> {
            if (!button.isSelected()) {
                button.setSelected(true);
                return;
            }
            onToggle.accept(index);
        });

        buttons.add(button);
        getChildren().add(button);
    }

    public void setOnToggle(Consumer<Integer> handler) {
        this.onToggle = handler;
    }

    public int getSelectedIndex() {
        for (int i = 0; i < buttons.size(); i++) {
            if (buttons.get(i).isSelected()) {
                return i;
            }
        }
        return -1;
    }

    public void setSelectedIndex(int index) {
        if (index >= 0 && index < buttons.size()) {
            buttons.get(index).setSelected(true);
        }
    }

    public ToggleButton getButton(int index) {
        return buttons.get(index);
    }
}
