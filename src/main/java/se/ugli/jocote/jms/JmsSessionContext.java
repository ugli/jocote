package se.ugli.jocote.jms;

import javax.jms.JMSException;
import javax.jms.Message;

import se.ugli.jocote.JocoteException;
import se.ugli.jocote.SessionContext;

class JmsSessionContext implements SessionContext {

    private boolean closable;
    private final Message message;
    private final se.ugli.jocote.Message jocoteJmsMessage;

    JmsSessionContext(final Message message) {
        this.message = message;
        this.jocoteJmsMessage = new JmsMessage(message);
    }

    @Override
    public se.ugli.jocote.Message message() {
        return jocoteJmsMessage;
    }

    @Override
    public void acknowledgeMessage() {
        closable = true;
        try {
            message.acknowledge();
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
