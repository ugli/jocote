package se.ugli.jocote.linkedqueue;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import se.ugli.jocote.Connection;
import se.ugli.jocote.Consumer;
import se.ugli.jocote.Driver;
import se.ugli.jocote.Subscription;

public class LinkedQueueDriver implements Driver {

    private static Map<String, LinkedQueueConnection> connections = new ConcurrentHashMap<String, LinkedQueueConnection>();
    private static final String URL_PREFIX = "linkedqueue@";

    @Override
    public boolean acceptsURL(final String url) {
        return url.startsWith(URL_PREFIX);
    }

    private LinkedQueueConnection connection(final String url) {
        final String name = getConnectionName(url);
        LinkedQueueConnection connection = connections.get(name);
        if (connection == null) {
            connection = new LinkedQueueConnection();
            connections.put(name, connection);
        }
        return connection;
    }

    private String getConnectionName(final String url) {
        return url.replace(URL_PREFIX, "");
    }

    @Override
    public Connection getConnection(final String url) {
        return connection(url);
    }

    @Override
    public Subscription subscribe(final String url, final Consumer<Object> consumer) {
        return connection(url).addSubscrition(consumer);
    }

}
