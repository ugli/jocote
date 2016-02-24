package se.ugli.jocote;

import java.util.Optional;
import java.util.function.Function;

public interface Connection extends AutoCloseable {

    @Override
    void close();

    Optional<byte[]> get();

    <T> Optional<T> get(Function<Message, Optional<T>> msgFunc);

    <T> Optional<T> getWithSession(Function<SessionContext, Optional<T>> sessionFunc);

    Iterator<byte[]> iterator();

    <T> Iterator<T> iterator(Function<Message, Optional<T>> msgFunc);

    SessionIterator<byte[]> sessionIterator();

    <T> SessionIterator<T> sessionIterator(Function<Message, Optional<T>> msgFunc);

    void put(byte[] message);

    void put(Message message);

}
