package org.streamsync.multimedia_client;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import shared.AppLogger;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.javafx.videosurface.ImageViewVideoSurface;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class PlayerController implements Initializable {
    @FXML VBox vBox;
    @FXML Label durLabel;
    @FXML Label nameLabel;
    @FXML StackPane stackPane;
    @FXML ImageView infoView;
    @FXML ImageView playerView;
    @FXML ImageView buttonImage;

    static EmbeddedMediaPlayer mediaPlayer; // static so that JVM garbage collector does not collect it

    private void handlePlayPause() {
        // Handle play/pause button
        if (mediaPlayer.status().isPlaying()) {
            mediaPlayer.controls().pause();
            buttonImage.setImage(new Image(
                    new File("src/main/resources/org/streamsync/multimedia_client/icons/play_arrow_40dp_FILL1_wght0_GRAD0_opsz40.png")
                            .toURI().toString()));
        } else {
            mediaPlayer.controls().play();
            buttonImage.setImage(new Image(
                    new File("src/main/resources/org/streamsync/multimedia_client/icons/pause_40dp_FILL1_wght0_GRAD0_opsz40.png")
                            .toURI().toString()));
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ListSingleton singleton = ListSingleton.getInstance();

        // THIS LINE SEARCHES FOR libvlc.dll AND SETS IT TO THE LIBRARY PATH - REQUIRES 64bit VLC INSTALLATION
        System.setProperty("jna.library.path", "C:\\Program Files\\VideoLAN\\VLC");
        // Create the media player
        MediaPlayerFactory factory = new MediaPlayerFactory();
        mediaPlayer = factory.mediaPlayers().newEmbeddedMediaPlayer();
        mediaPlayer.videoSurface().set(new ImageViewVideoSurface(this.playerView));

        // Protocol check
        if (singleton.getProtocol().equals("rtp/udp")) {
            mediaPlayer.media().play("..\\Multimedia_Server\\video.sdp");
            //mediaPlayer.media().play("video.sdp");
        } else {
            mediaPlayer.media().play(singleton.getProtocol() + "://@localhost:8080");
        }
        AppLogger.logger.info("Initialized Media Player.");

        // Set video height and width to match parent
        VBox.setVgrow(stackPane, javafx.scene.layout.Priority.ALWAYS);
        stackPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        playerView.fitWidthProperty().bind(stackPane.widthProperty());
        playerView.fitHeightProperty().bind(stackPane.heightProperty());

        // Listener for play/pause button
        buttonImage.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                handlePlayPause();
            }
        });

        // Listener for Video player
        playerView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                handlePlayPause();
            }
        });

        // Listener for info button
        infoView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                try {
                    Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("views/about.fxml")));
                    Stage stage = new Stage();
                    stage.setTitle("About");
                    stage.setScene(new Scene(root, 854, 480));
                    stage.show();
                    AppLogger.logger.info("Loaded About view.");
                } catch (IOException e) {
                    AppLogger.logger.error("Failed to load About view.");
                }
            }
        });

        // Update video timer every second - DOES NOT WORK WHILE USING RTP/UDP
        Timeline timer = new Timeline(new KeyFrame(Duration.seconds(1.0), e -> {
            if (mediaPlayer.status().isPlaying()) {
                long sec = ((mediaPlayer.status().time()) / 1000L) % 60;
                long min = ((mediaPlayer.status().time()) / 1000L) / 60;
                durLabel.setText(String.format("%02d:%02d", min, sec));
            }
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
        AppLogger.logger.info("Video timer started.");

        nameLabel.setText("Currently Playing: " + singleton.getSelectedFile().getName());
        //mediaPlayer.release();
        //factory.release();
    }
}
