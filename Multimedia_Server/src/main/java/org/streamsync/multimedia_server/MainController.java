package org.streamsync.multimedia_server;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import shared.AppLogger;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    BorderPane borderPane;
    Parent home;
    Parent console;
    Parent client;
    Parent about;

    public void openHome() {
        borderPane.setCenter(home);
        AppLogger.logger.info("Showing home scene.");
    }

    public void openConsole() {
        borderPane.setCenter(console);
        AppLogger.logger.info("Showing console scene.");
    }

    public void openClient() {
        borderPane.setCenter(client);
        AppLogger.logger.info("Showing client scene.");
    }

    public void openAbout() {
        borderPane.setCenter(about);
        AppLogger.logger.info("Showing about scene.");
    }

    public void exit() {
        System.exit(0);
    }

    private Parent loadScene(String sc) throws IOException {
        AppLogger.logger.info("Loaded scene: {}.", sc);
        return FXMLLoader.load(Objects.requireNonNull(getClass().getResource(sc)));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            home = loadScene("views/home.fxml");
            console = loadScene("views/console.fxml");
            client = loadScene("views/client.fxml");
            about = loadScene("views/about.fxml");

            borderPane.setCenter(home); // start with this window
            AppLogger.logger.info("Showing Home scene.");
        } catch (IOException e) {
            AppLogger.logger.error("Failed to load scene.");
        }
    }

}