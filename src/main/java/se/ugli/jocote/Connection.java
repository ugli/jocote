package se.ugli.jocote;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import se.ugli.jocote.support.Streams;

public interface Connection extends AutoCloseable {

    @Override
    void close();

    long clear();

    long messageCount();

    MessageIterator messageIterator();

    SessionIterator sessionIterator();

    void put(Message message);

    default Optional<Message> get() {
        return messageIterator().next();
    }

    default Stream<Message> messageStream() {
        return Streams.messageStream(messageIterator());
    }

    default SessionStream sessionStream() {
        return Streams.sessionStream(sessionIterator());
    }

    default void put(final byte[] message) {
        put(Message.builder().body(message).build());
    }

    default int put(final Stream<Message> messageStream) {
        final List<Boolean> counter = new ArrayList<>();
        messageStream.forEach(m -> {
            put(m);
            counter.add(true);
        });
        return counter.size();
    }
}
