package se.ugli.jocote;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public interface Connection extends AutoCloseable {

    @Override
    void close();

    Optional<byte[]> get();

    <T> Optional<T> get(Function<Message, Optional<T>> msgFunc);

    <T> Optional<T> getWithSession(Function<SessionContext, Optional<T>> sessionFunc);

    Stream<Message> stream();

    Stream<Message> stream(int batchSize);

    SessionStream sessionStream();

    SessionStream sessionStream(int batchSize);

    void put(byte[] message);

    void put(Message message);

    @Deprecated
    Iterator<byte[]> iterator();

    @Deprecated
    <T> Iterator<T> iterator(Function<Message, Optional<T>> msgFunc);

    @Deprecated
    SessionIterator<byte[]> sessionIterator();

    @Deprecated
    <T> SessionIterator<T> sessionIterator(Function<Message, Optional<T>> msgFunc);
}
