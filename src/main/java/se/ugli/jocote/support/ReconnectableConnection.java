package se.ugli.jocote.support;

import java.util.function.Consumer;
import java.util.function.Function;

import se.ugli.jocote.Connection;
import se.ugli.jocote.Jocote;
import se.ugli.jocote.Message;
import se.ugli.jocote.MessageIterator;
import se.ugli.jocote.SessionIterator;

public class ReconnectableConnection implements Connection {

    private Connection connection;
    private final String url;

    public ReconnectableConnection(final String url) {
        this.url = url;
    }

    private Connection connection() {
        if (connection == null) {
            // TODO make synchronized
            connection = Jocote.connect(url);
        }
        return connection;
    }

    @Override
    public void close() {
        if (connection != null) {
            connection.close();
            connection = null;
        }
    }

    private void safeConsume(final Consumer<Connection> connectionConsumer) {
        try {
            connectionConsumer.accept(connection());
        } catch (final RuntimeException e) {
            close();
            throw e;
        }
    }

    private <T> T safeFunction(final Function<Connection, T> connectionFunction) {
        try {
            return connectionFunction.apply(connection());
        } catch (final RuntimeException e) {
            close();
            throw e;
        }
    }

    @Override
    public long clear() {
        return safeFunction(Connection::clear);
    }

    @Override
    public MessageIterator messageIterator() {
        return safeFunction(Connection::messageIterator);
    }

    @Override
    public SessionIterator sessionIterator() {
        return safeFunction(Connection::sessionIterator);
    }

    @Override
    public void put(final Message message) {
        safeConsume(c -> c.put(message));
    }

    @Override
    public long messageCount() {
        return safeFunction(Connection::messageCount);
    }

}
