package org.streamsync.multimedia_server;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import shared.VideoFile;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ConsoleController implements Initializable {
    @FXML private TextArea consoleText;
    @FXML private TextField consoleInput;

    Thread socketThread; // Thread that handles the client connection
    Thread generateThread; // Thread that handles the generation of files

    DataSingleton singleton;

    private void writeToConsole(String message) {
        consoleText.appendText(message + "\n");
    }

    private String readFromConsole() {
        return consoleInput.getText();
    }

    public void generateFilesCommand(Server server) {
        // Creating a thread using lambda expression
        // So that GUI does not hang on command execution
        if (generateThread == null) {
            writeToConsole("Generating files..");
            generateThread = new Thread(() -> {
                server.generate();
                writeToConsole("Files generated.");
            });
            generateThread.start();
        } else {
            writeToConsole("Files already generated.");
        }
    }

    public void listFilesCommand(Server server) {
        ArrayList<VideoFile> tmp = server.getVideos();
        for (VideoFile f : tmp) {
            writeToConsole(String.format("Name: %-25s Format: %-15s Resolution: %sx%s",
                    f.getName(), f.getFormat(), f.getWidth(), f.getHeight()));
        }
        writeToConsole("\nNumber of files: " + tmp.size());
    }

    public void awaitConnectionCommand(Server server) {
        // Creating a thread using lambda expression
        // So that GUI does not hang on command execution
        if (socketThread == null) {
            socketThread = new Thread(() -> {
                server.connect(5000);
                writeToConsole("Connection established. Check Client menu for more information.");
                writeToConsole("Currently streaming: \"" + singleton.getSelectedFile().getName() + "\" over " + singleton.getProtocol() + ".");
            });
            socketThread.start();
            writeToConsole("Waiting for a connection on port 5000..");
        } else {
            writeToConsole("Connection already open.");
        }
    }

    public void closeConnectionCommand(Server server) {
        // close socket
        if (socketThread != null) {
            if (server.getSocket() != null) {
                if (!server.getSocket().isClosed()) {
                    server.close();
                }
            }

            // close thread
            socketThread.interrupt();
            socketThread = null;

            writeToConsole("Connection closed.");
        } else {
            writeToConsole("No active connection. Execute 'awaitconnection'.");
        }
    }

    public void listClientFilesCommand(Server server) {
        if (socketThread != null) {
            ArrayList<VideoFile> tmp = server.getClientVideos();
            for (VideoFile f : tmp) {
                writeToConsole(String.format("Name: %-25s Format: %-15s Resolution: %sx%s",
                        f.getName(), f.getFormat(), f.getWidth(), f.getHeight()));
            }
            writeToConsole("\nNumber of files: " + tmp.size());
        } else {
            writeToConsole("No active connection. Execute 'awaitconnection'.");
        }
    }

    public void clearCommand() {
        consoleText.clear();
        writeToConsole("Welcome to StreamSync!");
    }

    public void helpCommand() {
        writeToConsole("Available commands:");
        writeToConsole("- generatefiles: Reads videos directory and generates missing files.");
        writeToConsole("- listfiles: Lists available files.");
        writeToConsole("- awaitconnection: Await a client connection on port 5000.");
        writeToConsole("- closeconnection: Closes the connection.");
        writeToConsole("- listclientfiles: Lists all files that are sent to the client on an active connection. " +
                "These are the files that the client can support based on their network speed.");
        writeToConsole("- clear: Clears the console.");
        writeToConsole("- help: Displays this help message.");
        writeToConsole("- exit: Exit the program.");
    }

    public void exitCommand() {
        System.exit(0);
    }

    private void handleInput(Server server) {
        // read from user
        String inputString = readFromConsole();
        // clear input field
        consoleInput.setText("");

        // handle input
        if (!inputString.isEmpty()) {
            writeToConsole(">> " + inputString);
            inputString = inputString.trim().toLowerCase();

            switch (inputString) {
                case "generatefiles":
                    generateFilesCommand(server);
                    break;

                case "listfiles":
                    listFilesCommand(server);
                    break;

                case "awaitconnection":
                    awaitConnectionCommand(server);
                    break;

                case "closeconnection":
                    closeConnectionCommand(server);
                    break;

                case "listclientfiles":
                    listClientFilesCommand(server);
                    break;

                case "clear":
                    clearCommand();
                    break;

                case "help":
                    helpCommand();
                    break;

                case "exit":
                    exitCommand();
                    break;

                default:
                    writeToConsole("Invalid command. Enter 'help' for more information.");
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Server server = new Server();
        singleton = DataSingleton.getInstance();
        writeToConsole("Welcome to StreamSync!");

        // Handle Enter key pressed on TextField
        consoleInput.setOnKeyPressed(new EventHandler<>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                    handleInput(server);
                }
            }
        });
    }
}
