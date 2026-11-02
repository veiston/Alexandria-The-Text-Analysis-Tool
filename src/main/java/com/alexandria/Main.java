package com.alexandria;

import com.alexandria.controller.MainController;
import com.alexandria.service.LocalizationService;
import com.alexandria.utils.ThemeSettings;
import com.alexandria.view.MainView;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {

        MainController mainController = new MainController();
        MainView mainView = mainController.getView();
		
        ThemeSettings.apply(mainView);

        Scene scene = new Scene(mainView, 1200, 600);
        scene.getStylesheets().add(
                getClass().getResource("/styles/index.css").toExternalForm());

        LocalizationService i18n = LocalizationService.getInstance();
        stage.setTitle(i18n.get("app.title"));
        i18n.addListener(locale -> stage.setTitle(i18n.get("app.title")));

        stage.setScene(scene);
        stage.show();

        mainController.startUserGuideIfNeeded();
    }

    public static void main(String[] args) {
        launch();
    }
}
