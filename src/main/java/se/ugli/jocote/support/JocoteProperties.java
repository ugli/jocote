package se.ugli.jocote.support;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JocoteProperties {

    private static Properties properties = load();

    private static Properties load() {
        final Logger logger = LoggerFactory.getLogger(JocoteProperties.class);
        final Properties properties = new Properties();
        try {
            final InputStream inputStream = JocoteProperties.class.getResourceAsStream("/jocote.properties");
            if (inputStream != null)
                properties.load(inputStream);
            else
                logger.info("/jocote.properties not found using defaults");
        }
        catch (final IOException e) {
            LoggerFactory.getLogger(JocoteProperties.class).error(e.getMessage(), e);
        }
        return properties;
    }

    public static boolean debugLogConnections() {
        return "true".equals(properties.getProperty("debugLogConnections"));
    }

}
