package se.ugli.jocote.ram;

import se.ugli.jocote.Connection;
import se.ugli.jocote.Consumer;
import se.ugli.jocote.Driver;
import se.ugli.jocote.Subscription;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RamDriver implements Driver {

    private static final Map<String, RamConnection> connections = new ConcurrentHashMap<String, RamConnection>();
    private static final String URL_PREFIX = "ram@";

    @Override
    public boolean acceptsURL(final String url) {
        return url.startsWith(URL_PREFIX);
    }

    @Override
    public Connection getConnection(final String url) {
        return connection(url);
    }

    @Override
    public <T> Subscription<T> subscribe(final String url, final Consumer<T> consumer) {
        return connection(url).addSubscription(consumer);
    }

    private RamConnection connection(final String url) {
        final String name = getConnectionName(url);
        RamConnection connection = connections.get(name);
        if (connection == null) {
            connection = new RamConnection();
            connections.put(name, connection);
        }
        return connection;
    }

    private String getConnectionName(final String url) {
        return url.replace(URL_PREFIX, "");
    }

}
