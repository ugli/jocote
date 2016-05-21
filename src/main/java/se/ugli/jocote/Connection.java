package se.ugli.jocote;

import se.ugli.jocote.support.SessionImpl;
import se.ugli.jocote.support.Streams;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public interface Connection extends AutoCloseable {

    @Override
    void close();

    void clear();

    MessageIterator messageIterator();

    SessionIterator sessionIterator();

    void put(Message message);

    default Optional<Message> get() {
        return messageIterator().next();
    }

    default <T> Optional<T> get(final Function<Session, Optional<T>> sessionFunc) {
        try (Session session = new SessionImpl(sessionIterator())) {
            return sessionFunc.apply(session);
        }
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
        List<Boolean> counter = new ArrayList<>();
        messageStream.forEach(m -> {
            put(m);
            counter.add(true);
        });
        return counter.size();
    }
}
