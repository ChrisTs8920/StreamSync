package org.streamsync.multimedia_client;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;
import shared.AppLogger;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML Label speedLabel;
    @FXML Label waitLabel;
    @FXML ComboBox comboBox;
    @FXML Button button;
    @FXML BorderPane borderPane;

    SpeedTest test;
    final String[] FORMATS = {"avi", "mkv", "mp4"};

    Client client;
    ListSingleton singleton;
    Timeline speedTestTimeline;

    public void sendButton() {
        // If there is a connection
        if (client.connect("localhost", 5000) != null) {
            client.firstComm(test.getSpeed(), comboBox.getValue().toString());

            singleton.setFiles(client.getVideoFiles());
            nextScreen("views/video-list.fxml");
        } else {
            speedTestTimeline.stop();
            waitLabel.setText("No connection found! Please restart.");
            button.setDisable(true);
        }
    }

    public void nextScreen(String fxml) {
        try {
            borderPane.setCenter(loadScene(fxml));
            AppLogger.logger.info("Scene Loaded: {}.", fxml);
        } catch (IOException e) {
            AppLogger.logger.error("Failed to load scene {}.", fxml);
        }
    }

    private Parent loadScene(String sc) throws IOException {
        return FXMLLoader.load(Objects.requireNonNull(getClass().getResource(sc)));
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        singleton = ListSingleton.getInstance();
        client = new Client();

        singleton.isPlayerScreenReadyProperty().addListener((observableValue, oldVal, newVal) -> {
            // if client selected a video, send data to server
            if (newVal) {
                client.secondComm(singleton.getSelectedFile(), singleton.getProtocol());
                client.close();
                nextScreen("views/video-player.fxml");
            }
        });

        // Start speed test
        test = new SpeedTest("http://speedtest.ftp.otenet.gr/files/test100Mb.db");
        test.start();

        // Update speed label every second for the duration of the test
        // Also checks if speed test is completed every second
        speedTestTimeline = new Timeline(new KeyFrame(Duration.seconds(1.0), e -> {
            speedLabel.setText(test.getSpeed() + " Mbp/s");

            if (test.isTestCompleted()) {
                waitLabel.setText("Done!");
                button.setDisable(false);
                test.close();
            }
        }));
        speedTestTimeline.setCycleCount(Timeline.INDEFINITE);
        speedTestTimeline.play();
        AppLogger.logger.info("Speed test started.");

        // Set formats to comboBox
        comboBox.setItems(FXCollections.observableArrayList(FORMATS));
        comboBox.getSelectionModel().selectFirst(); // default to 'avi'
    }
}