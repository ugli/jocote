package se.ugli.jocote.jms;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.QueueBrowser;
import javax.jms.Session;

class CloseUtil {

    static void close(final Connection connection) {
        try {
            if (connection != null) {
                connection.stop();
                connection.close();
            }
        }
        catch (final JMSException e) {
            e.printStackTrace();
        }
    }

    static void close(final MessageConsumer messageConsumer) {
        try {
            if (messageConsumer != null)
                messageConsumer.close();
        }
        catch (final JMSException e) {
            e.printStackTrace();
        }
    }

    static void close(final MessageProducer messageProducer) {
        try {
            if (messageProducer != null)
                messageProducer.close();
        }
        catch (final JMSException e) {
            e.printStackTrace();
        }
    }

    static void close(final QueueBrowser queueBrowser) {
        try {
            if (queueBrowser != null)
                queueBrowser.close();
        }
        catch (final JMSException e) {
            e.printStackTrace();
        }
    }

    static void close(final Session session) {
        try {
            if (session != null)
                session.close();
        }
        catch (final JMSException e) {
            e.printStackTrace();
        }
    }

}
