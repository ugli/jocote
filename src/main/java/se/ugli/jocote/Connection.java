package se.ugli.jocote;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public interface Connection extends AutoCloseable {

    @Override
    void close();

    void clear();

    Optional<Message> get();

    <T> Optional<T> get(Function<Session, Optional<T>> consumer);

    MessageStream messageStream();

    SessionStream sessionStream();

    void put(byte[] message);

    void put(Message message);

    int put(Stream<Message> messageStream);
}
