package se.ugli.jocote.jndi;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import se.ugli.jocote.Consumer;
import se.ugli.jocote.Driver;
import se.ugli.jocote.JocoteException;
import se.ugli.jocote.Subscription;
import se.ugli.jocote.jms.JmsConnection;
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
    public JmsConnection getConnection(final String url) {
        try {
            return new JmsConnection(connectionFactory(url), destination(url));
        }
        catch (final NamingException e) {
            throw new JocoteException(e);
        }
    }

    private Destination destination(final String url) throws NamingException {
        final String subUrl = url.replace(URL_PREFIX, "");
        final String jndiName = subUrl.substring(subUrl.indexOf(":") + 1, subUrl.length());
        return (Destination) context.lookup(jndiName);
    }

    private ConnectionFactory connectionFactory(final String url) throws NamingException {
        final String subUrl = url.replace(URL_PREFIX, "");
        final String jndiName = subUrl.substring(0, subUrl.indexOf(":"));
        return (ConnectionFactory) context.lookup(jndiName);
    }

    @Override
    public Subscription subscribe(final String url, final Consumer<Object> consumer) {
        try {
            return new JmsSubscription(connectionFactory(url), consumer);
        }
        catch (final NamingException e) {
            throw new JocoteException(e);
        }
    }

}
