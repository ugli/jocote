package se.ugli.jocote.jms;

import static se.ugli.jocote.jms.AcknowledgeMode.AUTO_ACKNOWLEDGE;
import static se.ugli.jocote.jms.AcknowledgeMode.CLIENT_ACKNOWLEDGE;
import static se.ugli.jocote.jms.ConsumerHelper.sendReceive;

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
import se.ugli.jocote.support.DefaultConsumer;

public class JmsConnection implements Connection {

    private final javax.jms.Connection _connection;
    private final Destination destination;
    private final long receiveTimeout = 10;

    private MessageConsumer _messageConsumer;
    private MessageProducer _messageProducer;
    private Session _session;

    public JmsConnection(final ConnectionFactory connectionFactory, final Destination destination, final JocoteUrl url) {
        this.destination = destination;
        try {
            _connection = connectionFactory.createConnection(url.username, url.password);
            _connection.start();
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
    public Optional<byte[]> get() {
        return get(new DefaultConsumer());
    }

    @Override
    public <T> Optional<T> get(final Consumer<T> consumer) {
        try {
            final Message message = jmsMessageConsumer().receive(receiveTimeout);
            if (message != null)
                return sendReceive(consumer, message);
            return Optional.empty();
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
            final JmsSessionContext cxt = new JmsSessionContext(message);
            final Optional<T> result = consumer.receive(MessageFactory.getBytes(message), cxt);
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
    public Iterator<byte[]> iterator() {
        return iterator(new DefaultConsumer());
    }

    @Override
    public <T> Iterator<T> iterator(final Consumer<T> consumer) {
        return new JmsIterator<T>(jmsMessageConsumer(), receiveTimeout, consumer);
    }

    @Override
    public void put(final byte[] message) {
        put(message, null, null);
    }

    @Override
    public void put(final byte[] message, final Map<String, Object> headers, final Map<String, Object> properties) {
        try {
            jmsMessageProducer().send(MessageFactory.createJmsMessage(jmsSession(), message, headers, properties));
        }
        catch (final JMSException e) {
            throw new JocoteException(e);
        }
    }

    @Override
    public SessionIterator<byte[]> sessionIterator() {
        return sessionIterator(new DefaultConsumer());
    }

    @Override
    public <T> SessionIterator<T> sessionIterator(final Consumer<T> consumer) {
        return new JmsSessionIterator<T>(jmsConnection(), destination, receiveTimeout, consumer);
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
                _session = jmsConnection().createSession(false, AUTO_ACKNOWLEDGE.mode);
            return _session;
        }
        catch (final JMSException e) {
            throw new JocoteException(e);
        }
    }

    public javax.jms.Connection jmsConnection() {
        return _connection;
    }

}