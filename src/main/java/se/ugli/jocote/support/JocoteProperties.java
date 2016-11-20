package se.ugli.jocote.support;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JocoteProperties {

    private static final String JOCOTE_PROPERTIES = "/jocote.properties";
    private static Properties properties = load();

    private JocoteProperties() {
    }

    private static Properties load() {
        final Logger logger = LoggerFactory.getLogger(JocoteProperties.class);
        final Properties properties = new Properties();
        if (JocoteProperties.class.getResource(JOCOTE_PROPERTIES) != null)
            try (InputStream inputStream = JocoteProperties.class.getResourceAsStream(JOCOTE_PROPERTIES)) {
                properties.load(inputStream);
            }
            catch (final IOException e) {
                LoggerFactory.getLogger(JocoteProperties.class).error(e.getMessage(), e);
            }
        else
            logger.info("/jocote.properties not found using defaults");
        return properties;
    }

    public static boolean debugLogConnections() {
        return "true".equals(properties.getProperty("debugLogConnections"));
    }

    public static Optional<Integer> rabbitmqSessionIteratorChannelCloseDelayMs() {
        return Optional.ofNullable(properties.getProperty("rabbitmqSessionIteratorChannelCloseDelayMs"))
                .map(Integer::parseInt);
    }

}
