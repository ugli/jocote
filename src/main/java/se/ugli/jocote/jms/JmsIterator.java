package se.ugli.jocote.jms;

import java.util.Optional;
import java.util.function.Function;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;

import se.ugli.jocote.Iterator;
import se.ugli.jocote.JocoteException;
import se.ugli.jocote.Message;

class JmsIterator<T> implements Iterator<T> {

    private final MessageConsumer jmsConsumer;
    private final Function<se.ugli.jocote.Message, Optional<T>> msgFunc;
    private final long receiveTimeout;

    JmsIterator(final MessageConsumer jmsConsumer, final long receiveTimeout, final Function<Message, Optional<T>> msgFunc) {
        this.receiveTimeout = receiveTimeout;
        this.msgFunc = msgFunc;
        this.jmsConsumer = jmsConsumer;
    }

    @Override
    public Optional<T> next() {
        try {
            final javax.jms.Message message = jmsConsumer.receive(receiveTimeout);
            if (message != null)
                return msgFunc.apply(MessageFactory.create(message));
            return Optional.empty();
        }
        catch (final JMSException e) {
            throw new JocoteException(e);
        }
    }

}
