package com.alexandria.view.components.shared;

import javafx.scene.control.ProgressIndicator;

public class LoadingIndicator extends ProgressIndicator {

    public LoadingIndicator() {
        setVisible(false);
        setManaged(false);
    }

    public void setLoading(boolean loading) {
        setVisible(loading);
        setManaged(loading);
    }
}
