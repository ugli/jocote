package se.ugli.jocote.jms;

import static se.ugli.jocote.jms.AcknowledgeMode.AUTO_ACKNOWLEDGE;
import static se.ugli.jocote.jms.AcknowledgeMode.CLIENT_ACKNOWLEDGE;
import static se.ugli.jocote.jms.ConsumerHelper.sendReceive;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
import se.ugli.jocote.JocoteUrl;
import se.ugli.jocote.SessionConsumer;
import se.ugli.jocote.SessionIterator;

public class JmsConnection implements Connection {

    private final javax.jms.Connection _connection;
    private final Destination destination;
    private final long receiveTimeout = 10;

    private final MessageConsumer _messageConsumer;
    private final MessageProducer _messageProducer;
    private final Session _session;

    public JmsConnection(final ConnectionFactory connectionFactory, final Destination destination, final JocoteUrl url) {
        this.destination = destination;
        try {
            _connection = connectionFactory.createConnection(url.username, url.password);
            _connection.start();
            _session = jmsConnection().createSession(false, AUTO_ACKNOWLEDGE.mode);
            _messageConsumer = jmsSession().createConsumer(destination);
            _messageProducer = jmsSession().createProducer(destination);
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
        CloseUtil.close(_connection);
    }

    @Override
    public Optional<Object> get() {
        return get(Object.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Optional<T> get(final Class<T> type) {
        return get((Consumer<T>) (message, cxt) -> Optional.ofNullable((T) message));
    }

    @Override
    public <T> Optional<T> get(final Consumer<T> consumer) {
        try {
            return sendReceive(consumer, jmsMessageConsumer().receive(receiveTimeout));
        }
        catch (final JMSException e) {
            throw new JocoteException(e);
        }
    }

    @Override
    public <T> Optional<T> get(final SessionConsumer<T> consumer) {
        Session session = null;
        MessageConsumer messageConsumer = null;
        try {
            session = jmsConnection().createSession(false, CLIENT_ACKNOWLEDGE.mode);
            messageConsumer = session.createConsumer(destination);
            final Message message = messageConsumer.receive(receiveTimeout);
            final JmsSessionMessageContext cxt = new JmsSessionMessageContext(message);
            final Optional<T> result = consumer.receive(MessageFactory.createObjectMessage(message), cxt);
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
    public Iterator<Object> iterator() {
        return iterator(Object.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Iterator<T> iterator(final Class<T> type) {
        return iterator((Consumer<T>) (message, cxt) -> Optional.ofNullable((T) message));
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
    public SessionIterator<Object> sessionIterator() {
        return sessionIterator(Object.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> SessionIterator<T> sessionIterator(final Class<T> type) {
        return sessionIterator((Consumer<T>) (message, cxt) -> Optional.ofNullable((T) message));
    }

    @Override
    public <T> SessionIterator<T> sessionIterator(final Consumer<T> consumer) {
        return new JmsSessionIterator<T>(jmsConnection(), destination, receiveTimeout, consumer);
    }

    public MessageConsumer jmsMessageConsumer() {
        return _messageConsumer;
    }

    public MessageProducer jmsMessageProducer() {
        return _messageProducer;
    }

    public Session jmsSession() {
        return _session;
    }

    public javax.jms.Connection jmsConnection() {
        return _connection;
    }

}