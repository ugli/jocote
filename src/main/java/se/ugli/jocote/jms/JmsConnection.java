package se.ugli.jocote.jms;

import static se.ugli.jocote.jms.AcknowledgeMode.AUTO_ACKNOWLEDGE;

import java.util.Collections;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueBrowser;

import se.ugli.jocote.Connection;
import se.ugli.jocote.JocoteException;
import se.ugli.jocote.Message;
import se.ugli.jocote.MessageIterator;
import se.ugli.jocote.SessionIterator;
import se.ugli.jocote.support.JocoteUrl;

public class JmsConnection extends JmsBase implements Connection {

    public static final String JMS_MESSAGE_TYPE = "jmsMessageType";

    private final JocoteUrl url;
    private final javax.jms.Connection _connection;
    private final Queue queue;
    private final long receiveTimeout = 10;

    private MessageConsumer _messageConsumer;
    private MessageProducer _messageProducer;
    private javax.jms.Session _session;
    private QueueBrowser _queueBrowser;

    public JmsConnection(final ConnectionFactory connectionFactory, final Queue queue, final JocoteUrl url) {
        this.url = url;
        this.queue = queue;
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
        close(_queueBrowser);
        close(_messageConsumer);
        close(_messageProducer);
        close(_session);
        close(_connection);
    }

    @SuppressWarnings("unchecked")
    @Override
    public long messageCount() {
        try {
            return Collections.list(jmsQueueBrowser().getEnumeration()).size();
        }
        catch (final JMSException e) {
            throw new JocoteException(e);
        }
    }

    @Override
    public long clear() {
        long size = 0;
        while (messageIterator().next().isPresent())
            size++;
        return size;
    }

    @Override
    public MessageIterator messageIterator() {
        return new JmsIterator(jmsMessageConsumer(), receiveTimeout);
    }

    @Override
    public SessionIterator sessionIterator() {
        return new JmsSessionIterator(jmsConnection(), queue, receiveTimeout);
    }

    @Override
    public void put(final Message message) {
        try {
            jmsMessageProducer().send(JmsMessageFactory.create(jmsSession(), message));
        }
        catch (final JMSException e) {
            throw new JocoteException(e);
        }
    }

    public MessageConsumer jmsMessageConsumer() {
        try {
            if (_messageConsumer == null)
                _messageConsumer = jmsSession().createConsumer(queue);
            return _messageConsumer;
        }
        catch (final JMSException e) {
            throw new JocoteException(e);
        }
    }

    public MessageProducer jmsMessageProducer() {
        try {
            if (_messageProducer == null)
                _messageProducer = jmsSession().createProducer(queue);
            return _messageProducer;
        }
        catch (final JMSException e) {
            throw new JocoteException(e);
        }
    }

    public javax.jms.Session jmsSession() {
        try {
            if (_session == null)
                _session = jmsConnection().createSession(false, AUTO_ACKNOWLEDGE.mode);
            return _session;
        }
        catch (final JMSException e) {
            throw new JocoteException(e);
        }
    }

    public QueueBrowser jmsQueueBrowser() {
        try {
            if (_queueBrowser == null)
                _queueBrowser = jmsSession().createBrowser(queue);
            return _queueBrowser;
        }
        catch (final JMSException e) {
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