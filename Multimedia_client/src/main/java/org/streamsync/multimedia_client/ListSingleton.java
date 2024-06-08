package org.streamsync.multimedia_client;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import shared.VideoFile;

import java.util.ArrayList;

// This class is used to share data between ListController, MainController and PlayerController classes.
public class ListSingleton {
    private static ListSingleton instance;

    private final BooleanProperty isPlayerScreenReady = new SimpleBooleanProperty(false);
    private ArrayList<VideoFile> files;
    private VideoFile selectedFile;
    private String protocol;

    private ListSingleton() {
        files = new ArrayList<>();
    }

    public static ListSingleton getInstance() {
        if (instance == null) {
            instance = new ListSingleton();
        }
        return instance;
    }

    public ArrayList<VideoFile> getFiles() {
        return files;
    }

    public void setFiles(ArrayList<VideoFile> files) {
        this.files = files;
    }

    public VideoFile getSelectedFile() {
        return selectedFile;
    }

    public void setSelectedFile(VideoFile selectedFile) {
        this.selectedFile = selectedFile;
    }

    public void setIsPlayerScreenReady(boolean selected) {
        this.isPlayerScreenReady.set(selected);
    }

    public BooleanProperty isPlayerScreenReadyProperty() {
        return isPlayerScreenReady;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
}
