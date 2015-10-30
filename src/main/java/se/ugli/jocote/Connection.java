package se.ugli.jocote;

import java.io.Closeable;
import java.util.Map;

public interface Connection extends Closeable {

    <T> T get();

    <T> T get(Consumer<T> consumer);

    <T> T get(SessionConsumer<T> consumer);

    <T> Iterable<T> iterable();

    <T> Iterable<T> iterable(Consumer<T> consumer);

    <T> Iterable<T> iterable(int limit);

    <T> Iterable<T> iterable(int limit, Consumer<T> consumer);

    void put(Object message);

    void put(Object message, Map<String, Object> headers, Map<String, Object> properties);

    <T> SessionIterable<T> sessionIterable();

    <T> SessionIterable<T> sessionIterable(Consumer<T> consumer);

    <T> SessionIterable<T> sessionIterable(int limit);

    <T> SessionIterable<T> sessionIterable(int limit, Consumer<T> consumer);

    <T> SessionIterator<T> sessionIterator();

    <T> SessionIterator<T> sessionIterator(Consumer<T> consumer);

}
