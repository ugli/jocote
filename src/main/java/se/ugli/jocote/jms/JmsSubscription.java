package se.ugli.jocote.jms;

import static se.ugli.jocote.jms.AcknowledgeMode.AUTO_ACKNOWLEDGE;

import java.util.function.Consumer;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;

import se.ugli.jocote.JocoteException;
import se.ugli.jocote.Message;
import se.ugli.jocote.Subscription;

public class JmsSubscription extends JmsBase implements Subscription, MessageListener {

    private final Connection connection;
    private final Consumer<Message> consumer;
    private final Session session;
    private final MessageConsumer messageConsumer;

    public JmsSubscription(final ConnectionFactory connectionFactory, final Consumer<Message> consumer, final Destination destination) {
        this.consumer = consumer;
        try {
            connection = connectionFactory.createConnection();
            session = connection.createSession(false, AUTO_ACKNOWLEDGE.mode);
            messageConsumer = session.createConsumer(destination);
            messageConsumer.setMessageListener(this);
            connection.start();
        }
        catch (final JMSException e) {
            throw new JocoteException(e);
        }

    }

    @Override
    public void close() {
        close(messageConsumer);
        close(session);
        close(connection);
    }

    @Override
    public void onMessage(final javax.jms.Message message) {
        consumer.accept(MessageFactory.create(message));
    }

}
