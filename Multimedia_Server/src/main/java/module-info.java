module org.streamsync.multimedia_server {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires ffmpeg;
    requires org.apache.logging.log4j;

    opens org.streamsync.multimedia_server to javafx.fxml;
    exports org.streamsync.multimedia_server;
    exports shared;
    opens shared to javafx.fxml;
}