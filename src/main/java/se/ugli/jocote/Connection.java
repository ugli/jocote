package se.ugli.jocote;

import java.util.Map;
import java.util.Optional;

public interface Connection extends AutoCloseable {

    @Override
    void close();

    Optional<Object> get();

    <T> Optional<T> get(Class<T> type);

    <T> Optional<T> get(Consumer<T> consumer);

    <T> Optional<T> get(SessionConsumer<T> consumer);

    Iterator<Object> iterator();

    <T> Iterator<T> iterator(Class<T> type);

    <T> Iterator<T> iterator(Consumer<T> consumer);

    SessionIterator<Object> sessionIterator();

    <T> SessionIterator<T> sessionIterator(Class<T> type);

    <T> SessionIterator<T> sessionIterator(Consumer<T> consumer);

    void put(Object message);

    void put(Object message, Map<String, Object> headers, Map<String, Object> properties);

}
