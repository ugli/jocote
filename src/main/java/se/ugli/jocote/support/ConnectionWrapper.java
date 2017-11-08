package se.ugli.jocote.support;

import java.util.Optional;
import java.util.stream.Stream;

import se.ugli.jocote.Connection;
import se.ugli.jocote.Message;
import se.ugli.jocote.MessageIterator;
import se.ugli.jocote.SessionIterator;
import se.ugli.jocote.SessionStream;

public class ConnectionWrapper implements Connection {

    protected final Connection connection;

    public ConnectionWrapper(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void close() {
        connection.close();
    }

    @Override
    public long clear() {
        return connection.clear();
    }

    @Override
    public long messageCount() {
        return connection.messageCount();
    }

    @Override
    public MessageIterator messageIterator() {
        return connection.messageIterator();
    }

    @Override
    public SessionIterator sessionIterator() {
        return connection.sessionIterator();
    }

    @Override
    public void put(Message message) {
        connection.put(message);

    }

    @Override
    public Optional<Message> get() {
        return connection.get();
    }

    @Override
    public Stream<Message> messageStream() {
        return connection.messageStream();
    }

    @Override
    public SessionStream sessionStream() {
        return connection.sessionStream();
    }

    @Override
    public void put(byte[] message) {
        connection.put(message);
    }

    @Override
    public int put(Stream<Message> messageStream) {
        return connection.put(messageStream);
    }

}
