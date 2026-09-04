package com.alexandria;

import com.alexandria.view.MainView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        MainView mainView = new MainView();

        Scene scene = new Scene(mainView, 1200, 600);
        scene.getStylesheets().add(
                getClass().getResource("/styles/index.css").toExternalForm());

        stage.setTitle("Alexandria - Text Analysis Tool");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
