package se.ugli.jocote.jndi;

import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import se.ugli.jocote.Connection;
import se.ugli.jocote.Consumer;
import se.ugli.jocote.Driver;
import se.ugli.jocote.JocoteException;
import se.ugli.jocote.Subscription;
import se.ugli.jocote.jms.JmsConnection;
import se.ugli.jocote.jms.JmsSubscription;
import se.ugli.jocote.support.JocoteUrl;

public class JndiDriver implements Driver {

    public static final String URL_SCHEME = "jndi";

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
    public String getUrlScheme() {
        return URL_SCHEME;
    }

    @Override
    public Connection getConnection(final JocoteUrl url) {
        try {
            return new JmsConnection(connectionFactory(url), queue(url), url);
        }
        catch (final NamingException e) {
            throw new JocoteException(e);
        }
    }

    private Queue queue(final JocoteUrl url) throws NamingException {
        return (Queue) context.lookup(url.queue);
    }

    private ConnectionFactory connectionFactory(final JocoteUrl url) throws NamingException {
        return (ConnectionFactory) context.lookup(url.host);
    }

    @Override
    public <T> Subscription<T> subscribe(final JocoteUrl url, final Consumer<T> consumer) {
        try {
            return new JmsSubscription<T>(connectionFactory(url), consumer, queue(url));
        }
        catch (final NamingException e) {
            throw new JocoteException(e);
        }
    }

}
