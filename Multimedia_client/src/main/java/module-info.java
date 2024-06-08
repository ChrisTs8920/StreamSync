module org.streamsync.multimedia_client {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires jspeedtest;
    requires uk.co.caprica.vlcj;
    requires uk.co.caprica.vlcj.javafx;
    requires org.apache.logging.log4j.core;
    requires org.apache.logging.log4j;

    opens org.streamsync.multimedia_client to javafx.fxml;
    exports org.streamsync.multimedia_client;
    exports shared;
    opens shared to javafx.fxml;
}