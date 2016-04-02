package se.ugli.jocote.jms;

import java.util.Optional;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;

import se.ugli.jocote.JocoteException;
import se.ugli.jocote.Message;
import se.ugli.jocote.support.MessageIterator;

class JmsIterator implements MessageIterator {

    private final MessageConsumer jmsConsumer;
    private final long receiveTimeout;
    private int index = 0;

    JmsIterator(final MessageConsumer jmsConsumer, final long receiveTimeout) {
        this.receiveTimeout = receiveTimeout;
        this.jmsConsumer = jmsConsumer;
    }

    @Override
    public Optional<Message> next() {
        try {
            final javax.jms.Message message = jmsConsumer.receive(receiveTimeout);
            if (message != null) {
                index++;
                return Optional.of(MessageFactory.create(message));
            }
            return Optional.empty();
        }
        catch (final JMSException e) {
            throw new JocoteException(e);
        }
    }

    @Override
    public int index() {
        return index;
    }

}
