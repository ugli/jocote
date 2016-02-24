package se.ugli.jocote;

import java.util.Map;
import java.util.Optional;

public interface Connection extends AutoCloseable {

    @Override
    void close();

    Optional<byte[]> get();

    <T> Optional<T> get(Consumer<T> consumer);

    <T> Optional<T> get(SessionConsumer<T> consumer);

    Iterator<byte[]> iterator();

    <T> Iterator<T> iterator(Consumer<T> consumer);

    SessionIterator<byte[]> sessionIterator();

    <T> SessionIterator<T> sessionIterator(Consumer<T> consumer);

    void put(byte[] message);

    void put(byte[] message, Map<String, Object> headers, Map<String, Object> properties);

}
