package se.ugli.jocote.jms;

import static se.ugli.jocote.jms.AcknowledgeMode.AUTO_ACKNOWLEDGE;
import static se.ugli.jocote.jms.ConsumerHelper.sendReceive;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;

import se.ugli.jocote.Consumer;
import se.ugli.jocote.JocoteException;
import se.ugli.jocote.Subscription;

public class JmsSubscription implements Subscription, MessageListener {

    private final Connection connection;
    private final Consumer<byte[]> consumer;
    private final Session session;
    private final MessageConsumer messageConsumer;

    public JmsSubscription(final ConnectionFactory connectionFactory, final Consumer<byte[]> consumer, final Destination destination) {
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
        CloseUtil.close(messageConsumer);
        CloseUtil.close(session);
        CloseUtil.close(connection);
    }

    @Override
    public void onMessage(final Message message) {
        sendReceive(consumer, message);
    }

}
