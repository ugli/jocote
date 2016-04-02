package se.ugli.jocote.ram;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import se.ugli.jocote.Connection;
import se.ugli.jocote.Driver;
import se.ugli.jocote.Message;
import se.ugli.jocote.Subscription;
import se.ugli.jocote.support.JocoteUrl;

public class RamDriver implements Driver {

    private static final Map<String, RamConnection> connections = new ConcurrentHashMap<String, RamConnection>();
    private static final String URL_SCHEME = "ram";

    @Override
    public String urlScheme() {
        return URL_SCHEME;
    }

    @Override
    public Connection connect(final JocoteUrl url) {
        return _connection(url);
    }

    @Override
    public Subscription subscribe(final JocoteUrl url, final Consumer<Message> consumer) {
        return _connection(url).addSubscription(consumer);
    }

    private RamConnection _connection(final JocoteUrl url) {
        RamConnection connection = connections.get(url.queue);
        if (connection == null) {
            connection = new RamConnection();
            connections.put(url.queue, connection);
        }
        return connection;
    }

}
