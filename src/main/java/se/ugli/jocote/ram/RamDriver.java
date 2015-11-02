package se.ugli.jocote.ram;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import se.ugli.jocote.Consumer;
import se.ugli.jocote.Driver;
import se.ugli.jocote.QueueConnection;
import se.ugli.jocote.Subscription;

public class RamDriver implements Driver {

    private static Map<String, RamQueueConnection> connections = new ConcurrentHashMap<String, RamQueueConnection>();
    private static final String URL_PREFIX = "ram@";

    @Override
    public boolean acceptsURL(final String url) {
        return url.startsWith(URL_PREFIX);
    }

    @Override
    public QueueConnection getQueueConnection(final String url) {
        return connection(url);
    }

    @Override
    public <T> Subscription<T> subscribe(final String url, final Consumer<T> consumer) {
        return connection(url).addSubscrition(consumer);
    }

    private RamQueueConnection connection(final String url) {
        final String name = getConnectionName(url);
        RamQueueConnection connection = connections.get(name);
        if (connection == null) {
            connection = new RamQueueConnection();
            connections.put(name, connection);
        }
        return connection;
    }

    private String getConnectionName(final String url) {
        return url.replace(URL_PREFIX, "");
    }

}
