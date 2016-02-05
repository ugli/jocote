package se.ugli.jocote;

import java.util.Map;
import java.util.Optional;

public interface Connection extends AutoCloseable {

    @Override
    void close();

    <T> Optional<T> get();

    <T> Optional<T> get(Consumer<T> consumer);

    <T> Optional<T> get(SessionConsumer<T> consumer);

    <T> Iterator<T> iterator();

    <T> Iterator<T> iterator(Consumer<T> consumer);

    <T> SessionIterator<T> sessionIterator();

    <T> SessionIterator<T> sessionIterator(Consumer<T> consumer);

    void put(Object message);

    void put(Object message, Map<String, Object> headers, Map<String, Object> properties);

}
