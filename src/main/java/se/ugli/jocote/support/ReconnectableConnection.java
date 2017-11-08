package se.ugli.jocote.support;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.ugli.jocote.Connection;
import se.ugli.jocote.Jocote;
import se.ugli.jocote.Message;
import se.ugli.jocote.MessageIterator;
import se.ugli.jocote.SessionIterator;
import se.ugli.jocote.SessionStream;

public class ReconnectableConnection implements Connection {

    private static final Logger LOG = LoggerFactory.getLogger(ReconnectableConnection.class);

    private Connection connection;
    private final String url;

    public ReconnectableConnection(final String url) {
        this.url = url;
        this.connection = Jocote.connect(url);
    }

    private synchronized void reconnect() {
        LOG.info("Reconnect: {}", url);
        connection.close();
        this.connection = Jocote.connect(url);
    }

    private void retryConsume(final Consumer<Connection> connectionConsumer) {
        try {
            connectionConsumer.accept(connection);
        } catch (final RuntimeException e1) {
            try {
                LOG.error(e1.getMessage(), e1);
                reconnect();
                LOG.warn("Retrying call...");
                connectionConsumer.accept(connection);
                LOG.info("Retry succeeded.");
            } catch (final RuntimeException e2) {
                LOG.error("Retry failed: " + e2.getMessage(), e2);
                throw e2;
            }
        }
    }

    private <T> T retryFunction(final Function<Connection, T> connectionFunction) {
        try {
            return connectionFunction.apply(connection);
        } catch (final RuntimeException e1) {
            try {
                LOG.error(e1.getMessage(), e1);
                reconnect();
                LOG.warn("Retrying call...");
                final T result = connectionFunction.apply(connection);
                LOG.info("Retry succeeded.");
                return result;
            } catch (final RuntimeException e2) {
                LOG.error("Retry failed: " + e2.getMessage(), e2);
                throw e2;
            }
        }
    }

    @Override
    public void close() {
        connection.close();
    }

    @Override
    public long clear() {
        return retryFunction(Connection::clear);
    }

    @Override
    public Optional<Message> get() {
        return retryFunction(Connection::get);
    }

    @Override
    public long messageCount() {
        return retryFunction(Connection::messageCount);
    }

    @Override
    public MessageIterator messageIterator() {
        return retryFunction(Connection::messageIterator);
    }

    @Override
    public Stream<Message> messageStream() {
        return retryFunction(Connection::messageStream);
    }

    @Override
    public void put(byte[] message) {
        retryConsume(c -> c.put(message));
    }

    @Override
    public void put(final Message message) {
        retryConsume(c -> c.put(message));
    }

    @Override
    public int put(Stream<Message> messageStream) {
        return retryFunction(c -> c.put(messageStream));
    }

    @Override
    public SessionIterator sessionIterator() {
        return retryFunction(Connection::sessionIterator);
    }

    @Override
    public SessionStream sessionStream() {
        return retryFunction(Connection::sessionStream);
    }

}
