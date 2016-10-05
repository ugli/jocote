package se.ugli.jocote.jms;

import static se.ugli.jocote.jms.AcknowledgeMode.CLIENT_ACKNOWLEDGE;
import static se.ugli.jocote.support.Id.newId;

import java.util.Optional;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;

import se.ugli.jocote.JocoteException;
import se.ugli.jocote.Message;
import se.ugli.jocote.SessionIterator;

class JmsSessionIterator extends JmsBase implements SessionIterator {

    private final long receiveTimeout;
    private final Session session;
    private final MessageConsumer jmsConsumer;

    private javax.jms.Message lastMessage;
    private boolean closable = false;
    private String sessionid;

    JmsSessionIterator(final Connection connection, final Destination destination, final long receiveTimeout) {
        try {
            this.receiveTimeout = receiveTimeout;
            session = connection.createSession(false, CLIENT_ACKNOWLEDGE.mode);
            sessionid = newId();
            jmsConsumer = session.createConsumer(destination);
        }
        catch (final JMSException e) {
            throw new JocoteException(e);
        }
    }

    @Override
    public void ack() {
        try {
            if (lastMessage != null) {
                closable = true;
                lastMessage.acknowledge();
            }
        }
        catch (final JMSException e) {
            throw new JocoteException(e);
        }
    }

    @Override
    public void close() {
        close(jmsConsumer);
        close(session);
        if (!closable && lastMessage != null)
            throw new JocoteException("You have to acknowledge or leave messages before closing");
    }

    @Override
    public void nack() {
        closable = true;
    }

    @Override
    public Optional<Message> next() {
        try {
            final javax.jms.Message message = jmsConsumer.receive(receiveTimeout);
            if (message != null)
                lastMessage = message;
            return Optional.ofNullable(MessageFactory.create(message));
        }
        catch (final JMSException e) {
            throw new JocoteException(e);
        }
    }

    @Override
    public String sessionid() {
        return sessionid;
    }

}
