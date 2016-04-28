package se.ugli.jocote.jms;

import se.ugli.jocote.Connection;
import se.ugli.jocote.*;
import se.ugli.jocote.Message;
import se.ugli.jocote.Session;
import se.ugli.jocote.support.JocoteUrl;
import se.ugli.jocote.support.MessageIterator;
import se.ugli.jocote.support.SessionIterator;
import se.ugli.jocote.support.Streams;

import javax.jms.*;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static se.ugli.jocote.jms.AcknowledgeMode.AUTO_ACKNOWLEDGE;
import static se.ugli.jocote.jms.AcknowledgeMode.CLIENT_ACKNOWLEDGE;

public class JmsConnection extends JmsBase implements Connection {

    public static final String JMS_MESSAGE_TYPE = "jmsMessageType";

    private final javax.jms.Connection _connection;
    private final Destination destination;
    private final long receiveTimeout = 10;

    private MessageConsumer _messageConsumer;
    private MessageProducer _messageProducer;
    private javax.jms.Session _session;

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
        close(_messageConsumer);
        close(_messageProducer);
        close(_session);
        close(_connection);
    }

    @Override
    public void clear() {
        while (iterator().next().isPresent())
            ;
    }

    @Override
    public Optional<Message> get() {
        try {
            final javax.jms.Message message = jmsMessageConsumer().receive(receiveTimeout);
            if (message != null)
                return Optional.of(MessageFactory.create(message));
            return Optional.empty();
        }
        catch (final JMSException e) {
            throw new JocoteException(e);
        }
    }

    @Override
    public <T> Optional<T> get(final Function<Session, Optional<T>> sessionFunc) {
        javax.jms.Session session = null;
        MessageConsumer messageConsumer = null;
        try {
            session = jmsConnection().createSession(false, CLIENT_ACKNOWLEDGE.mode);
            messageConsumer = session.createConsumer(destination);
            final javax.jms.Message message = messageConsumer.receive(receiveTimeout);
            if (message == null)
                return Optional.empty();
            final JmsSession cxt = new JmsSession(message);
            final Optional<T> result = sessionFunc.apply(cxt);
            if (cxt.isClosable())
                return result;
            throw new JocoteException("You have to acknowledge or leave message");
        }
        catch (final JMSException e) {
            throw new JocoteException(e);
        }
        finally {
            close(messageConsumer);
            close(session);
        }
    }

    private MessageIterator iterator() {
        return new JmsIterator(jmsMessageConsumer(), receiveTimeout);
    }

    @Override
    public MessageStream messageStream() {
        return Streams.messageStream(iterator());
    }

    @Override
    public MessageStream messageStream(final int batchSize) {
        return Streams.messageStream(iterator(), batchSize);
    }

    @Override
    public SessionStream sessionStream() {
        return Streams.sessionStream(sessionIterator());
    }

    @Override
    public SessionStream sessionStream(final int batchSize) {
        return Streams.sessionStream(sessionIterator(), batchSize);
    }

    @Override
    public void put(final byte[] message) {
        put(Message.builder().body(message).build());
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

    @Override
    public int put(final Stream<Message> messageStream) {
        List<Message> messages = messageStream.collect(toList());
        messages.forEach(this::put);
        return messages.size();
    }

    private SessionIterator sessionIterator() {
        return new JmsSessionIterator(jmsConnection(), destination, receiveTimeout);
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

    public javax.jms.Connection jmsConnection() {
        return _connection;
    }

}