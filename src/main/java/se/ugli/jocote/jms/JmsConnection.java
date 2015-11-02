package se.ugli.jocote.jms;

import static se.ugli.jocote.jms.AcknowledgeMode.AUTO_ACKNOWLEDGE;
import static se.ugli.jocote.jms.AcknowledgeMode.CLIENT_ACKNOWLEDGE;
import static se.ugli.jocote.jms.ConsumerHelper.sendReceive;

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
import se.ugli.jocote.Iterator;
import se.ugli.jocote.JocoteException;
import se.ugli.jocote.SessionConsumer;
import se.ugli.jocote.SessionIterator;
import se.ugli.jocote.support.SimpleConsumer;

public class JmsConnection implements Connection {

    private final javax.jms.Connection connection;
    private final Destination destination;
    private final long receiveTimeout = 10;

    private MessageConsumer _messageConsumer;
    private MessageProducer _messageProducer;
    private Session _session;

    public JmsConnection(final ConnectionFactory connectionFactory, final Destination destination) {
        this.destination = destination;
        try {
            this.connection = connectionFactory.createConnection();
            connection.start();
        }
        catch (final JMSException e) {
            throw new JocoteException(e);
        }
    }

    @Override
    public void close() {
        CloseUtil.close(_messageConsumer);
        CloseUtil.close(_messageProducer);
        CloseUtil.close(_session);
        CloseUtil.close(connection);
    }

    @Override
    public <T> T get() {
        return get(new SimpleConsumer<T>());
    }

    @Override
    public <T> T get(final Consumer<T> consumer) {
        try {
            return sendReceive(consumer, jmsMessageConsumer().receive(receiveTimeout));
        }
        catch (final JMSException e) {
            throw new JocoteException(e);
        }
    }

    @Override
    public <T> T get(final SessionConsumer<T> consumer) {
        Session session = null;
        MessageConsumer messageConsumer = null;
        try {
            session = connection.createSession(false, CLIENT_ACKNOWLEDGE.mode);
            messageConsumer = session.createConsumer(destination);
            final Message message = messageConsumer.receive(receiveTimeout);
            final JmsSessionMessageContext cxt = new JmsSessionMessageContext(message);
            final T result = consumer.receive(MessageFactory.createObjectMessage(message), cxt);
            if (cxt.isClosable())
                return result;
            throw new JocoteException("You have to acknowledge or leave message");
        }
        catch (final JMSException e) {
            throw new JocoteException(e);
        }
        finally {
            CloseUtil.close(messageConsumer);
            CloseUtil.close(session);
        }
    }

    @Override
    public <T> Iterator<T> iterator() {
        return iterator(new SimpleConsumer<T>());
    }

    @Override
    public <T> Iterator<T> iterator(final Consumer<T> consumer) {
        return new JmsIterator<T>(jmsMessageConsumer(), receiveTimeout, consumer);
    }

    @Override
    public void put(final Object message) {
        put(message, new HashMap<String, Object>(), new HashMap<String, Object>());
    }

    @Override
    public void put(final Object message, final Map<String, Object> headers, final Map<String, Object> properties) {
        try {
            jmsMessageProducer().send(MessageFactory.createJmsMessage(jmsSession(), message, headers, properties));
        }
        catch (final JMSException e) {
            throw new JocoteException(e);
        }
    }

    @Override
    public <T> SessionIterator<T> sessionIterator() {
        return sessionIterator(new SimpleConsumer<T>());
    }

    @Override
    public <T> SessionIterator<T> sessionIterator(final Consumer<T> consumer) {
        return new JmsSessionIterator<T>(connection, destination, receiveTimeout, consumer);
    }

    public MessageConsumer jmsMessageConsumer() {
        try {
            if (_messageConsumer == null)
                _messageConsumer = jmsSession().createConsumer(destination);
            return _messageConsumer;
        }
        catch (final JMSException e) {
            throw new JocoteException(e);
        }
    }

    public MessageProducer jmsMessageProducer() {
        try {
            if (_messageProducer == null)
                _messageProducer = jmsSession().createProducer(destination);
            return _messageProducer;
        }
        catch (final JMSException e) {
            throw new JocoteException(e);
        }
    }

    public Session jmsSession() {
        try {
            if (_session == null)
                _session = connection.createSession(false, AUTO_ACKNOWLEDGE.mode);
            return _session;
        }
        catch (final JMSException e) {
            throw new JocoteException(e);
        }
    }

    public javax.jms.Connection jmsConnection() {
        return connection;
    }

}