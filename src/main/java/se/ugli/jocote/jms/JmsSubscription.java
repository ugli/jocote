package se.ugli.jocote.jms;

import static se.ugli.jocote.jms.AcknowledgeMode.AUTO_ACKNOWLEDGE;
import static se.ugli.jocote.jms.ConsumerHelper.sendReceive;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;

import se.ugli.jocote.Consumer;
import se.ugli.jocote.JocoteException;
import se.ugli.jocote.Subscription;

public class JmsSubscription implements Subscription, MessageListener {

    private final Connection connection;
    private final Consumer<Object> consumer;
    private final Session session;

    public JmsSubscription(final ConnectionFactory connectionFactory, final Consumer<Object> consumer) {
        this.consumer = consumer;
        try {
            this.connection = connectionFactory.createConnection();
            connection.start();
            this.session = connection.createSession(false, AUTO_ACKNOWLEDGE.mode);
            session.setMessageListener(this);
        }
        catch (final JMSException e) {
            throw new JocoteException(e);
        }

    }

    @Override
    public void close() {
        CloseUtil.close(session);
        CloseUtil.close(connection);
    }

    @Override
    public void onMessage(final Message message) {
        sendReceive(consumer, message);
    }

}
