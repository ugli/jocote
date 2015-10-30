package se.ugli.jocote.jms;

import javax.jms.JMSException;
import javax.jms.Message;

import se.ugli.jocote.JocoteException;
import se.ugli.jocote.SessionMessageContext;

class JmsSessionMessageContext extends JmsMessageContext implements SessionMessageContext {

    JmsSessionMessageContext(final Message message) {
        super(message);
    }

    @Override
    public void acknowledgeMessage() {
        try {
            message.acknowledge();
        }
        catch (final JMSException e) {
            throw new JocoteException(e);
        }
    }

    @Override
    public void leaveMessage() {
    }

}
