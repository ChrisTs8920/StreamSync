package org.streamsync.multimedia_client;

import shared.AppLogger;
import shared.VideoFile;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class Client {
    private Socket socket;
    private DataInputStream input;
    private DataOutputStream out;
    private ObjectInputStream objInput;
    private ObjectOutputStream objOut;

    private ArrayList<VideoFile> videoFiles;

    public Client() {

    }

    public Socket connect(String host, int port) {
        try {
            socket = new Socket(host, port);
            input = new DataInputStream(System.in);
            out = new DataOutputStream(socket.getOutputStream());
            objInput = new ObjectInputStream(socket.getInputStream());
            objOut = new ObjectOutputStream(socket.getOutputStream());

            AppLogger.logger.info("Connected to {}:{}.", host, port);
            return socket;
        } catch (IOException e) {
            AppLogger.logger.error("Could not connect to the server.");
            return null;
        }
    }

    public void firstComm(int speed, String format) {
        // First communication between server - client
        send(speed);
        send(format);
        videoFiles = receiveList();
    }

    public void secondComm(VideoFile selectedFile, String protocol) {
        // Second communication between server - client
        sendFile(selectedFile);
        send(protocol);
    }

    public void close() {
        try {
            input.close();
            out.close();
            socket.close();
            AppLogger.logger.info("Closing connection to {}:{}.", socket.getInetAddress(), socket.getPort());
        } catch (IOException e) {
            AppLogger.logger.error("Could not close the client socket.");
        }
    }

    private void sendFile(VideoFile file) {
        try {
            objOut.writeObject(file);
            AppLogger.logger.info("Sent selected file: {}", file);
        } catch (IOException e) {
            AppLogger.logger.error("Could not send file to the server.");
        }
    }

    private  <T> void send(T data) {
        try {
            out.writeUTF(data.toString());
            AppLogger.logger.info("Sent data: {}", data);
        } catch (IOException e) {
            AppLogger.logger.error("Could not send data to the server.");
        }
    }

    private ArrayList<VideoFile> receiveList() {
        try {
            return (ArrayList<VideoFile>) objInput.readObject();
        } catch (IOException | ClassNotFoundException e) {
            AppLogger.logger.error("Could not receive video list from the server.");
            return null;
        }
    }

    public ArrayList<VideoFile> getVideoFiles() {
        return videoFiles;
    }
}
