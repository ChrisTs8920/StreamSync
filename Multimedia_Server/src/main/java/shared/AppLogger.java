package shared;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// This class is inside package 'shared', because it is shared with the client
public class AppLogger {
    public static Logger logger = LogManager.getLogger(AppLogger.class);
}
