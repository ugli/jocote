package se.ugli.jocote.jms;

import static se.ugli.jocote.jms.AcknowledgeMode.CLIENT_ACKNOWLEDGE;
import static se.ugli.jocote.jms.ConsumerHelper.sendReceive;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;

import se.ugli.jocote.Consumer;
import se.ugli.jocote.JocoteException;
import se.ugli.jocote.SessionIterable;

class JmsSessionIterable<T> implements SessionIterable<T> {

    private final Session session;
    private final Iterator<T> iterator;

    private boolean closable = false;
    private Message lastMessage;

    JmsSessionIterable(final Connection connection, final Destination destination, final long receiveTimeout, final int limit,
            final Consumer<T> jocoteConsumer) {
        MessageConsumer jmsConsumer = null;
        try {
            session = connection.createSession(false, CLIENT_ACKNOWLEDGE.mode);
            jmsConsumer = session.createConsumer(destination);
            final List<T> messages = new LinkedList<T>();
            Message next = jmsConsumer.receive(receiveTimeout);
            while (next != null && limit != messages.size()) {
                messages.add(sendReceive(jocoteConsumer, next));
                lastMessage = next;
                next = jmsConsumer.receive(receiveTimeout);
            }
            iterator = messages.iterator();
        }
        catch (final JMSException e) {
            throw new JocoteException(e);
        }
        finally {
            CloseUtil.close(jmsConsumer);
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
        CloseUtil.close(session);
    }

    @Override
    public Iterator<T> iterator() {
        return iterator;
    }

    @Override
    public void leaveMessages() {
        closable = true;
    }

}
