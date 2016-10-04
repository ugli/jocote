package se.ugli.jocote.jms;

import se.ugli.jocote.Connection;
import se.ugli.jocote.*;
import se.ugli.jocote.Message;
import se.ugli.jocote.support.JocoteUrl;

import javax.jms.*;

import static se.ugli.jocote.jms.AcknowledgeMode.AUTO_ACKNOWLEDGE;

public class JmsConnection extends JmsBase implements Connection {

    public static final String JMS_MESSAGE_TYPE = "jmsMessageType";

    private final JocoteUrl url;
    private final javax.jms.Connection _connection;
    private final Destination destination;
    private final long receiveTimeout = 10;

    private MessageConsumer _messageConsumer;
    private MessageProducer _messageProducer;
    private javax.jms.Session _session;

    public JmsConnection(final ConnectionFactory connectionFactory, final Destination destination, final JocoteUrl url) {
        this.url = url;
        this.destination = destination;
        try {
            _connection = connectionFactory.createConnection(url.username, url.password);
            _connection.start();
        } catch (final JMSException e) {
            throw new JocoteException(e);
        }
    }

    @Override
    public void close() {
        close(_messageConsumer);
        close(_messageProducer);
        close(_session);
        close(_connection);
    }

    @Override
    public void clear() {
        while (messageIterator().next().isPresent())
            ;
    }

    @Override
    public MessageIterator messageIterator() {
        return new JmsIterator(jmsMessageConsumer(), receiveTimeout);
    }

    @Override
    public SessionIterator sessionIterator() {
        return new JmsSessionIterator(jmsConnection(), destination, receiveTimeout);
    }

    @Override
    public void put(final Message message) {
        try {
            jmsMessageProducer().send(JmsMessageFactory.create(jmsSession(), message));
        } catch (final JMSException e) {
            throw new JocoteException(e);
        }
    }

    public MessageConsumer jmsMessageConsumer() {
        try {
            if (_messageConsumer == null)
                _messageConsumer = jmsSession().createConsumer(destination);
            return _messageConsumer;
        } catch (final JMSException e) {
            throw new JocoteException(e);
        }
    }

    public MessageProducer jmsMessageProducer() {
        try {
            if (_messageProducer == null)
                _messageProducer = jmsSession().createProducer(destination);
            return _messageProducer;
        } catch (final JMSException e) {
            throw new JocoteException(e);
        }
    }

    public javax.jms.Session jmsSession() {
        try {
            if (_session == null)
                _session = jmsConnection().createSession(false, AUTO_ACKNOWLEDGE.mode);
            return _session;
        } catch (final JMSException e) {
            throw new JocoteException(e);
        }
    }

    public javax.jms.Connection jmsConnection() {
        return _connection;
    }

    @Override
    public String toString() {
        return url.toString();
    }
}