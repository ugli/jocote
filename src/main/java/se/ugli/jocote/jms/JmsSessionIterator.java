package se.ugli.jocote.jms;

import static se.ugli.jocote.jms.AcknowledgeMode.CLIENT_ACKNOWLEDGE;

import java.util.Optional;
import java.util.function.Function;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;

import se.ugli.jocote.JocoteException;
import se.ugli.jocote.Message;
import se.ugli.jocote.SessionIterator;

class JmsSessionIterator<T> extends JmsBase implements SessionIterator<T> {

    private final long receiveTimeout;
    private final Session session;
    private final MessageConsumer jmsConsumer;
    private final Function<Message, Optional<T>> msgFunc;

    private javax.jms.Message lastMessage;
    private boolean closable = false;

    JmsSessionIterator(final Connection connection, final Destination destination, final long receiveTimeout,
            final Function<Message, Optional<T>> msgFunc) {
        try {
            this.msgFunc = msgFunc;
            this.receiveTimeout = receiveTimeout;
            session = connection.createSession(false, CLIENT_ACKNOWLEDGE.mode);
            jmsConsumer = session.createConsumer(destination);
        }
        catch (final JMSException e) {
            throw new JocoteException(e);
        }
    }

    @Override
    public void acknowledgeMessages() {
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
    public void leaveMessages() {
        closable = true;
    }

    @Override
    public Optional<T> next() {
        try {
            final javax.jms.Message message = jmsConsumer.receive(receiveTimeout);
            if (message != null) {
                lastMessage = message;
                return msgFunc.apply(MessageFactory.create(message));
            }
            return Optional.empty();
        }
        catch (final JMSException e) {
            throw new JocoteException(e);
        }
    }

}
