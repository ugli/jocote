package se.ugli.jocote.jms;

import static se.ugli.jocote.jms.AcknowledgeMode.AUTO_ACKNOWLEDGE;
import static se.ugli.jocote.jms.AcknowledgeMode.CLIENT_ACKNOWLEDGE;
import static se.ugli.jocote.jms.ConsumerHelper.sendReceive;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;

import se.ugli.jocote.Connection;
import se.ugli.jocote.Consumer;
import se.ugli.jocote.JocoteException;
import se.ugli.jocote.SessionConsumer;
import se.ugli.jocote.SessionIterable;
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
            return sendReceive(consumer, messageConsumer().receive(receiveTimeout));
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
            return sendReceive(consumer, messageConsumer.receive(receiveTimeout));
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
    public <T> Iterable<T> iterable() {
        return iterable(Integer.MAX_VALUE, new SimpleConsumer<T>());
    }

    @Override
    public <T> Iterable<T> iterable(final Consumer<T> consumer) {
        return iterable(Integer.MAX_VALUE, consumer);
    }

    @Override
    public <T> Iterable<T> iterable(final int limit) {
        return iterable(limit, new SimpleConsumer<T>());
    }

    @Override
    public <T> Iterable<T> iterable(final int limit, final Consumer<T> consumer) {
        final List<T> result = new LinkedList<T>();
        T next = get(consumer);
        while (next != null && limit != result.size()) {
            result.add(next);
            next = get(consumer);
        }
        return result;
    }

    @Override
    public void put(final Object message) {
        put(message, new HashMap<String, Object>(), new HashMap<String, Object>());
    }

    @Override
    public void put(final Object message, final Map<String, Object> headers, final Map<String, Object> properties) {
        try {
            messageProducer().send(MessageFactory.createJmsMessage(session(), message, headers, properties));
        }
        catch (final JMSException e) {
            throw new JocoteException(e);
        }
    }

    @Override
    public <T> SessionIterable<T> sessionIterable() {
        return sessionIterable(Integer.MAX_VALUE, new SimpleConsumer<T>());
    }

    @Override
    public <T> SessionIterable<T> sessionIterable(final Consumer<T> consumer) {
        return sessionIterable(Integer.MAX_VALUE, consumer);
    }

    @Override
    public <T> SessionIterable<T> sessionIterable(final int limit) {
        return sessionIterable(limit, new SimpleConsumer<T>());
    }

    @Override
    public <T> SessionIterable<T> sessionIterable(final int limit, final Consumer<T> consumer) {
        return new JmsSessionIterable<T>(connection, destination, limit, limit, consumer);
    }

    @Override
    public <T> SessionIterator<T> sessionIterator() {
        return sessionIterator(new SimpleConsumer<T>());
    }

    @Override
    public <T> SessionIterator<T> sessionIterator(final Consumer<T> consumer) {
        if (destination instanceof Queue)
            return new JmsSessionIterator<T>(connection, (Queue) destination, receiveTimeout, consumer);
        else
            throw new JocoteException("Jms SessionIterator can only use queues as destination");
    }

    private MessageConsumer messageConsumer() {
        try {
            if (_messageConsumer == null)
                _messageConsumer = session().createConsumer(destination);
            return _messageConsumer;
        }
        catch (final JMSException e) {
            throw new JocoteException(e);
        }
    }

    private MessageProducer messageProducer() {
        try {
            if (_messageProducer == null)
                _messageProducer = session().createProducer(destination);
            return _messageProducer;
        }
        catch (final JMSException e) {
            throw new JocoteException(e);
        }
    }

    private Session session() {
        try {
            if (_session == null)
                _session = connection.createSession(false, AUTO_ACKNOWLEDGE.mode);
            return _session;
        }
        catch (final JMSException e) {
            throw new JocoteException(e);
        }
    }

}