package se.ugli.jocote.jndi;

import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import se.ugli.jocote.Consumer;
import se.ugli.jocote.Driver;
import se.ugli.jocote.JocoteException;
import se.ugli.jocote.QueueConnection;
import se.ugli.jocote.Subscription;
import se.ugli.jocote.jms.JmsQueueConnection;
import se.ugli.jocote.jms.JmsSubscription;

public class JndiDriver implements Driver {

    private static final String URL_PREFIX = "jms:jndi@";

    private static InitialContext createContext() {
        try {
            return new InitialContext();
        }
        catch (final NamingException e) {
            throw new JocoteException(e);
        }
    }

    private final InitialContext context;

    public JndiDriver() {
        this(createContext());
    }

    JndiDriver(final InitialContext context) {
        this.context = context;
    }

    @Override
    public boolean acceptsURL(final String url) {
        return url.startsWith(URL_PREFIX);
    }

    @Override
    public QueueConnection getQueueConnection(final String url) {
        try {
            return new JmsQueueConnection(connectionFactory(url), queue(url));
        }
        catch (final NamingException e) {
            throw new JocoteException(e);
        }
    }

    private Queue queue(final String url) throws NamingException {
        final String subUrl = url.replace(URL_PREFIX, "");
        final String jndiName = subUrl.substring(subUrl.indexOf(":") + 1, subUrl.length());
        return (Queue) context.lookup(jndiName);
    }

    private ConnectionFactory connectionFactory(final String url) throws NamingException {
        final String subUrl = url.replace(URL_PREFIX, "");
        final String jndiName = subUrl.substring(0, subUrl.indexOf(":"));
        return (ConnectionFactory) context.lookup(jndiName);
    }

    @Override
    public <T> Subscription<T> subscribe(final String url, final Consumer<T> consumer) {
        try {
            return new JmsSubscription<T>(connectionFactory(url), consumer, queue(url));
        }
        catch (final NamingException e) {
            throw new JocoteException(e);
        }
    }

}
