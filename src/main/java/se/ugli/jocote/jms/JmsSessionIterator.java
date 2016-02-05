package se.ugli.jocote.jms;

import static se.ugli.jocote.jms.AcknowledgeMode.CLIENT_ACKNOWLEDGE;
import static se.ugli.jocote.jms.ConsumerHelper.sendReceive;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;

import se.ugli.jocote.Consumer;
import se.ugli.jocote.JocoteException;
import se.ugli.jocote.SessionIterator;

class JmsSessionIterator<T> implements SessionIterator<T> {

    private final long receiveTimeout;
    private final Session session;
    private final MessageConsumer jmsConsumer;
    private final Consumer<T> jocoteConsumer;

    private Message lastMessage;
    private boolean closable = false;

    JmsSessionIterator(final Connection connection, final Destination destination, final long receiveTimeout,
            final Consumer<T> jocoteConsumer) {
        try {
            this.jocoteConsumer = jocoteConsumer;
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
        CloseUtil.close(jmsConsumer);
        CloseUtil.close(session);
        if (!closable && lastMessage != null)
            throw new JocoteException("You have to acknowledge or leave messages before closing");
    }

    @Override
    public void leaveMessages() {
        closable = true;
    }

    @Override
    public T next() {
        try {
            final javax.jms.Message message = jmsConsumer.receive(receiveTimeout);
            if (message != null) {
                lastMessage = message;
                return sendReceive(jocoteConsumer, message);
            }
            // TODO java.util.Optional
            return null;
        }
        catch (final JMSException e) {
            throw new JocoteException(e);
        }
    }

}
