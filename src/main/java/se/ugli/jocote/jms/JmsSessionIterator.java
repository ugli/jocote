package se.ugli.jocote.jms;

import static se.ugli.jocote.jms.AcknowledgeMode.CLIENT_ACKNOWLEDGE;
import static se.ugli.jocote.jms.ConsumerHelper.sendReceive;

import java.io.IOException;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.Session;

import se.ugli.jocote.Consumer;
import se.ugli.jocote.JocoteException;
import se.ugli.jocote.SessionIterator;

class JmsSessionIterator<T> implements SessionIterator<T> {

    private final long receiveTimeout;
    private final Session session;
    private final MessageConsumer jmsConsumer;
    private final Consumer<T> jocoteConsumer;
    private final QueueBrowser queueBrowser;

    private Message lastMessage;
    private boolean closable = false;

    JmsSessionIterator(final Connection connection, final Queue queue, final long receiveTimeout, final Consumer<T> jocoteConsumer) {
        try {
            this.jocoteConsumer = jocoteConsumer;
            this.receiveTimeout = receiveTimeout;
            session = connection.createSession(false, CLIENT_ACKNOWLEDGE.mode);
            jmsConsumer = session.createConsumer(queue);
            queueBrowser = session.createBrowser(queue);
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
    public void close() throws IOException {
        if (!closable && lastMessage != null)
            throw new JocoteException("You have to acknowledge or leave messages before closing");
        CloseUtil.close(queueBrowser);
        CloseUtil.close(jmsConsumer);
        CloseUtil.close(session);
    }

    @Override
    public boolean hasNext() {
        try {
            return queueBrowser.getEnumeration().hasMoreElements();
        }
        catch (final JMSException e) {
            throw new JocoteException(e);
        }
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
