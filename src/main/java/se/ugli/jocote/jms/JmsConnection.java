package se.ugli.jocote.jms;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;

import se.ugli.jocote.Connection;
import se.ugli.jocote.Consumer;
import se.ugli.jocote.support.SimpleConsumer;

public class JmsConnection implements Connection {

    private final boolean transacted = false;
    private final int acknowledgeMode = AcknowledgeMode.AUTO_ACKNOWLEDGE.mode;
    private final long receiveTimeout = 10;

    private final javax.jms.Connection connection;
    private final Session session;
    private final MessageConsumer messageConsumer;
    private final MessageProducer messageProducer;

    public JmsConnection(final ConnectionFactory connectionFactory, final Destination destination) {
        try {
            this.connection = connectionFactory.createConnection();
            this.session = connection.createSession(transacted, acknowledgeMode);
            this.messageConsumer = session.createConsumer(destination);
            this.messageProducer = session.createProducer(destination);
            connection.start();
        }
        catch (final JMSException e) {
            throw new JmsException(e);
        }
    }

    @Override
    public void close() {
        try {
            messageConsumer.close();
        }
        catch (final JMSException e) {
            e.printStackTrace();
        }
        try {
            messageProducer.close();
        }
        catch (final JMSException e) {
            e.printStackTrace();
        }
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
    public <T> T getOne(final Consumer<T> consumer) {
        try {
            final Message message = messageConsumer.receive(receiveTimeout);
            return consumer.receive(MessageFactory.createObjectMessage(message), new JmsMessageContext(message));
        }
        catch (final JMSException e) {
            throw new JmsException(e);
        }
    }

    @Override
    public <T> T getOne() {
        return getOne(new SimpleConsumer<T>());
    }

    @Override
    public void put(final Object message) {
        put(message, new HashMap<String, Object>(), new HashMap<String, Object>());
    }

    @Override
    public void put(final Object message, final Map<String, Object> headers, final Map<String, Object> properties) {
        try {
            messageProducer.send(MessageFactory.createJmsMessage(session, message, headers, properties));
        }
        catch (final JMSException e) {
            throw new JmsException(e);
        }
        catch (final NoSuchMethodException e) {
            throw new JmsException(e);
        }
        catch (final SecurityException e) {
            throw new JmsException(e);
        }
        catch (final IllegalAccessException e) {
            throw new JmsException(e);
        }
        catch (final IllegalArgumentException e) {
            throw new JmsException(e);
        }
        catch (final InvocationTargetException e) {
            throw new JmsException(e);
        }
    }

}
