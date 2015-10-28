package se.ugli.jocote.jms;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;

import se.ugli.jocote.Consumer;
import se.ugli.jocote.Subscription;

public class JmsSubscription implements Subscription, MessageListener {

    private final boolean transacted = false;
    private final int acknowledgeMode = AcknowledgeMode.AUTO_ACKNOWLEDGE.mode;
    private final Connection connection;
    private final Session session;
    private final Consumer<Object> consumer;

    public JmsSubscription(final ConnectionFactory connectionFactory, final Consumer<Object> consumer) {
        this.consumer = consumer;
        try {
            this.connection = connectionFactory.createConnection();
            this.session = connection.createSession(transacted, acknowledgeMode);
            session.setMessageListener(this);
        }
        catch (final JMSException e) {
            throw new JmsException(e);
        }

    }

    @Override
    public void close() {
        try {
            session.close();
        }
        catch (final JMSException e) {
            e.printStackTrace();
        }
        try {
            connection.close();
        }
        catch (final JMSException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessage(final Message message) {
        try {
            consumer.receive(MessageFactory.createObjectMessage(message), new JmsMessageContext(message));
        }
        catch (final JMSException e) {
            throw new JmsException(e);
        }
    }

}
