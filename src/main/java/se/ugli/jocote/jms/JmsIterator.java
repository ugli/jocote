package se.ugli.jocote.jms;

import static se.ugli.jocote.jms.ConsumerHelper.sendReceive;

import java.util.Optional;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;

import se.ugli.jocote.Consumer;
import se.ugli.jocote.Iterator;
import se.ugli.jocote.JocoteException;

class JmsIterator<T> implements Iterator<T> {

    private final MessageConsumer jmsConsumer;
    private final Consumer<T> jocoteConsumer;
    private final long receiveTimeout;

    JmsIterator(final MessageConsumer jmsConsumer, final long receiveTimeout, final Consumer<T> jocoteConsumer) {
        this.receiveTimeout = receiveTimeout;
        this.jocoteConsumer = jocoteConsumer;
        this.jmsConsumer = jmsConsumer;
    }

    @Override
    public Optional<T> next() {
        try {
            final Message message = jmsConsumer.receive(receiveTimeout);
            if (message != null)
                return sendReceive(jocoteConsumer, message);
            return Optional.empty();
        }
        catch (final JMSException e) {
            throw new JocoteException(e);
        }
    }

}
