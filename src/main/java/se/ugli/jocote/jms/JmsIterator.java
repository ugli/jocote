package se.ugli.jocote.jms;

import se.ugli.jocote.JocoteException;
import se.ugli.jocote.Message;
import se.ugli.jocote.MessageIterator;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import java.util.Optional;

class JmsIterator implements MessageIterator {

    private final MessageConsumer jmsConsumer;
    private final long receiveTimeout;

    JmsIterator(final MessageConsumer jmsConsumer, final long receiveTimeout) {
        this.receiveTimeout = receiveTimeout;
        this.jmsConsumer = jmsConsumer;
    }

    @Override
    public Optional<Message> next() {
        try {
            return Optional.ofNullable(MessageFactory.create(jmsConsumer.receive(receiveTimeout)));
        }
        catch (final JMSException e) {
            throw new JocoteException(e);
        }
    }

}