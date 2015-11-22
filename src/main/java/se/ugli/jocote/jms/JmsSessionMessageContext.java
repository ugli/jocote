package se.ugli.jocote.jms;

import se.ugli.jocote.JocoteException;
import se.ugli.jocote.SessionMessageContext;

import javax.jms.JMSException;
import javax.jms.Message;

class JmsSessionMessageContext extends JmsMessageContext implements SessionMessageContext {

    private boolean closable;
    private Message message;

    JmsSessionMessageContext(final Message message) {
        super(message);
        this.message = message;
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
