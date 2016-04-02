package se.ugli.jocote.jms;

import javax.jms.JMSException;

import se.ugli.jocote.JocoteException;
import se.ugli.jocote.Message;
import se.ugli.jocote.Session;

class JmsSession implements Session {

    private boolean closable;
    private final javax.jms.Message jmsMessage;
    private final Message jocoteMessage;

    JmsSession(final javax.jms.Message jmsMessage) {
        this.jmsMessage = jmsMessage;
        this.jocoteMessage = MessageFactory.create(jmsMessage);
    }

    @Override
    public se.ugli.jocote.Message message() {
        return jocoteMessage;
    }

    @Override
    public void ack() {
        closable = true;
        try {
            jmsMessage.acknowledge();
        }
        catch (final JMSException e) {
            throw new JocoteException(e);
        }
    }

    @Override
    public void nack() {
        closable = true;
    }

    boolean isClosable() {
        return closable;
    }

}
