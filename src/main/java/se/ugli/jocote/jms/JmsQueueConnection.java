package se.ugli.jocote.jms;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueBrowser;

import se.ugli.jocote.JocoteException;
import se.ugli.jocote.QueueConnection;

public class JmsQueueConnection extends JmsConnection implements QueueConnection {

    private final Queue queue;
    private QueueBrowser _queueBrowser;

    public JmsQueueConnection(final ConnectionFactory connectionFactory, final Queue queue) {
        super(connectionFactory, queue);
        this.queue = queue;
    }

    @Override
    public boolean hasNext() {
        try {
            return queueBrowser().getEnumeration().nextElement() != null;
        }
        catch (final JMSException e) {
            throw new JocoteException(e);
        }
    }

    public QueueBrowser queueBrowser() {
        try {
            if (_queueBrowser == null)
                _queueBrowser = jmsSession().createBrowser(queue);
            return _queueBrowser;
        }
        catch (final JMSException e) {
            throw new JocoteException(e);
        }
    }

}
