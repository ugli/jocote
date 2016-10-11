package se.ugli.jocote.jms;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.QueueBrowser;
import javax.jms.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class JmsBase {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    protected void close(final Connection connection) {
        try {
            if (connection != null) {
                connection.stop();
                connection.close();
            }
        }
        catch (final RuntimeException | JMSException e) {
            logger.warn("Couldn't close connection: " + e.getMessage());
        }
    }

    protected void close(final MessageConsumer messageConsumer) {
        try {
            if (messageConsumer != null)
                messageConsumer.close();
        }
        catch (final RuntimeException | JMSException e) {
            logger.warn("Couldn't close message consumer: " + e.getMessage());
        }
    }

    protected void close(final QueueBrowser queueBrowser) {
        try {
            if (queueBrowser != null)
                queueBrowser.close();
        }
        catch (final RuntimeException | JMSException e) {
            logger.warn("Couldn't close queueBrowser: " + e.getMessage());
        }
    }

    protected void close(final MessageProducer messageProducer) {
        try {
            if (messageProducer != null)
                messageProducer.close();
        }
        catch (final RuntimeException | JMSException e) {
            logger.warn("Couldn't close message producer: " + e.getMessage());
        }
    }

    protected void close(final Session session) {
        try {
            if (session != null)
                session.close();
        }
        catch (final RuntimeException | JMSException e) {
            logger.warn("Couldn't close session: " + e.getMessage());
        }
    }

}
