package se.ugli.jocote.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

class RabbitMqBase {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    protected void close(final Channel channel) {
        try {
            channel.close();
        }
        catch (final RuntimeException | TimeoutException | IOException e) {
            logger.warn("Couldn't close channel: " + e.getMessage());
        }
    }

    protected void close(final Connection connection) {
        try {
            connection.close();
        }
        catch (final RuntimeException | IOException e) {
            logger.warn("Couldn't close connection: " + e.getMessage());
        }
    }

}