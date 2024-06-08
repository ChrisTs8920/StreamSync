package org.streamsync.multimedia_server;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class ClientController implements Initializable {
    @FXML Label connectionLabel;
    @FXML Label addressLabel;
    @FXML Label portLabel;
    @FXML Label speedLabel;
    @FXML Label formatLabel;

    DataSingleton singleton;

    private void handlePropertyTrue() {
        connectionLabel.setText("Client connected.");
        addressLabel.setText("Address: " + singleton.getIpaddr());
        portLabel.setText("Port: " + singleton.getPort());
        speedLabel.setText("Speed: " + singleton.getClientSpeed() + "Mbp/s");
        formatLabel.setText("Format: " + singleton.getClientFormat());
    }

    private void handlePropertyFalse() {
        connectionLabel.setText("No client connection found.");
        addressLabel.setText("");
        portLabel.setText("");
        speedLabel.setText("");
        formatLabel.setText("");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        singleton = DataSingleton.getInstance();
        singleton.connectedProperty().addListener((observableValue, oldVal, newVal) -> {
            // if a client connected
            if (newVal) {
                handlePropertyTrue();
            } else {
                handlePropertyFalse();
            }
        });
    }
}
