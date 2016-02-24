package se.ugli.jocote.jms;

import javax.jms.JMSException;

import se.ugli.jocote.JocoteException;
import se.ugli.jocote.Message;
import se.ugli.jocote.SessionContext;

class JmsSessionContext implements SessionContext {

    private boolean closable;
    private final javax.jms.Message jmsMessage;
    private final Message jocoteMessage;

    JmsSessionContext(final javax.jms.Message jmsMessage) {
        this.jmsMessage = jmsMessage;
        this.jocoteMessage = MessageFactory.create(jmsMessage);
    }

    @Override
    public se.ugli.jocote.Message message() {
        return jocoteMessage;
    }

    @Override
    public void acknowledgeMessage() {
        closable = true;
        try {
            jmsMessage.acknowledge();
        }
        catch (final JMSException e) {
            throw new JocoteException(e);
        }
    }

    @Override
    public void leaveMessage() {
        closable = true;
    }

    boolean isClosable() {
        return closable;
    }

}
