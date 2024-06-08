package org.streamsync.multimedia_server;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import shared.VideoFile;

// This class is used to share data between the ConsoleController (through the Server class) and ClientController classes.
public class DataSingleton {
    private final BooleanProperty connected = new SimpleBooleanProperty(false);
    private String ipaddr;
    private int port;

    private int clientSpeed;
    private String clientFormat;
    private VideoFile selectedFile;
    private String protocol;

    private static DataSingleton instance;

    private DataSingleton() {

    }

    public static DataSingleton getInstance() {
        if (instance == null) {
            instance = new DataSingleton();
        }
        return instance;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getIpaddr() {
        return ipaddr;
    }

    public void setIpaddr(String ipaddr) {
        this.ipaddr = ipaddr;
    }

    public int getClientSpeed() {
        return clientSpeed;
    }

    public void setClientSpeed(int clientSpeed) {
        this.clientSpeed = clientSpeed;
    }

    public String getClientFormat() {
        return clientFormat;
    }

    public void setClientFormat(String clientFormat) {
        this.clientFormat = clientFormat;
    }

    public boolean isConnected() {
        return connected.get();
    }

    public void setConnected(boolean connected) {
        this.connected.set(connected);
    }

    public BooleanProperty connectedProperty() {
        return connected;
    }

    public VideoFile getSelectedFile() {
        return selectedFile;
    }

    public void setSelectedFile(VideoFile selectedFile) {
        this.selectedFile = selectedFile;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
}
