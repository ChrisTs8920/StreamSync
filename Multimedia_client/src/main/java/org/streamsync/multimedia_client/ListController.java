package org.streamsync.multimedia_client;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import shared.AppLogger;
import shared.VideoFile;

import java.net.URL;
import java.util.ResourceBundle;

public class ListController implements Initializable {
    @FXML private ListView<VideoFile> listView;
    @FXML private ComboBox comboBox;

    final String[] PROTOCOLS = {"tcp", "udp", "rtp/udp"};

    ListSingleton singleton;

    public void protocolSelection() {
        if (comboBox.getSelectionModel().getSelectedItem() != null) {
            singleton.setProtocol(comboBox.getValue().toString());
        } else { // If User did not select a protocol, select based on given rules
            if (singleton.getSelectedFile().getHeight() == 240) {
                singleton.setProtocol(PROTOCOLS[0]);
            }
            else if (singleton.getSelectedFile().getHeight() == 360 || singleton.getSelectedFile().getHeight() == 480) {
                singleton.setProtocol(PROTOCOLS[1]);
            }
            else if (singleton.getSelectedFile().getHeight() == 720 || singleton.getSelectedFile().getHeight() == 1080) {
                singleton.setProtocol(PROTOCOLS[2]);
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        singleton = ListSingleton.getInstance();

        // Add files to list
        ObservableList<VideoFile> fileList = FXCollections.observableArrayList(singleton.getFiles());
        listView.setItems(fileList);
        AppLogger.logger.info("Added files to list.");
        // Customize every list cell (row)
        listView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(VideoFile videoFile, boolean empty) {
                super.updateItem(videoFile, empty);

                if (empty || videoFile == null) {
                    setText(null);
                } else {
                    setText(String.format("Name: %-25s Format: %-15s Resolution: %sx%s",
                            videoFile.getName(), videoFile.getFormat(), videoFile.getWidth(), videoFile.getHeight()));
                }
            }
        });

        // Listener for list
        listView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                // On double-click
                if (mouseEvent.getClickCount() == 2) {
                    singleton.setSelectedFile(listView.getSelectionModel().getSelectedItem());

                    protocolSelection();
                    singleton.setIsPlayerScreenReady(true);
                }
            }
        });

        // set protocols to combobox
        comboBox.setItems(FXCollections.observableArrayList(PROTOCOLS));
    }
}
