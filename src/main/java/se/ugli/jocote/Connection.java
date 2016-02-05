package se.ugli.jocote;

import java.io.Closeable;
import java.util.Map;

public interface Connection extends Closeable {

    @Override
    void close();

    <T> T get();

    <T> T get(Consumer<T> consumer);

    <T> T get(SessionConsumer<T> consumer);

    <T> Iterator<T> iterator();

    <T> Iterator<T> iterator(Consumer<T> consumer);

    <T> SessionIterator<T> sessionIterator();

    <T> SessionIterator<T> sessionIterator(Consumer<T> consumer);

    void put(Object message);

    void put(Object message, Map<String, Object> headers, Map<String, Object> properties);

}
