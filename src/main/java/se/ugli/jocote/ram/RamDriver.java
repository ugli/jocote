package se.ugli.jocote.ram;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import se.ugli.jocote.Connection;
import se.ugli.jocote.Consumer;
import se.ugli.jocote.Driver;
import se.ugli.jocote.Subscription;
import se.ugli.jocote.support.JocoteUrl;

public class RamDriver implements Driver {

    private static final Map<String, RamConnection> connections = new ConcurrentHashMap<String, RamConnection>();
    private static final String URL_SCHEME = "ram";

    @Override
    public String getUrlScheme() {
        return URL_SCHEME;
    }

    @Override
    public Connection getConnection(final JocoteUrl url) {
        return connection(url);
    }

    @Override
    public <T> Subscription<T> subscribe(final JocoteUrl url, final Consumer<T> consumer) {
        return connection(url).addSubscription(consumer);
    }

    private RamConnection connection(final JocoteUrl url) {
        RamConnection connection = connections.get(url.queue);
        if (connection == null) {
            connection = new RamConnection();
            connections.put(url.queue, connection);
        }
        return connection;
    }

}
