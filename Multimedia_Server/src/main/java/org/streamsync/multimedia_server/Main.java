package org.streamsync.multimedia_server;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Start server GUI
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("views/main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
        scene.getStylesheets().add(Objects.requireNonNull(
                getClass().getResource("css/styles.css")).toExternalForm());
        stage.setTitle("Server");
        stage.setScene(scene);
        stage.show();

        // Initialize Singleton
        DataSingleton singleton = DataSingleton.getInstance();
    }

    public static void main(String[] args) {
        launch();
    }
}