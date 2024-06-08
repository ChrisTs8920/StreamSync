package org.streamsync.multimedia_client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("views/main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
        stage.setTitle("Client");
        stage.setScene(scene);
        stage.show();

        // Initialize Singleton
        ListSingleton singleton = ListSingleton.getInstance();
    }

    public static void main(String[] args) {
        launch();
    }
}