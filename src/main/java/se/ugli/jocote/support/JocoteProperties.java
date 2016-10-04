package se.ugli.jocote.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class JocoteProperties {

    private static Properties properties = load();

    private static Properties load() {
        Logger logger = LoggerFactory.getLogger(JocoteProperties.class);
        Properties properties = new Properties();
        try {
            InputStream inputStream = JocoteProperties.class.getResourceAsStream("/jocote.properties");
            if (inputStream != null)
                properties.load(inputStream);
            else
                logger.info("/jocote.properties not found using defaults");
        }
        catch (IOException e) {
            LoggerFactory.getLogger(JocoteProperties.class).error(e.getMessage(),e);
        }
        return properties;
    }

    public static boolean traceLogConnections() {
        return "true".equals(properties.getProperty("traceLogConnections"));
    }


}
