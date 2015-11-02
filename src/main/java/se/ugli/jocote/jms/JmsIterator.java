package se.ugli.jocote.jms;

import static se.ugli.jocote.jms.ConsumerHelper.sendReceive;

import javax.jms.JMSException;
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
    public T next() {
        try {
            return sendReceive(jocoteConsumer, jmsConsumer.receive(receiveTimeout));
        }
        catch (final JMSException e) {
            throw new JocoteException(e);
        }
    }

}
