package se.ugli.jocote;

import java.io.Closeable;
import java.util.Map;

public interface Connection extends Closeable {

    <T> T getOne();

    <T> T getOne(Consumer<T> consumer);

    void put(Object message);

    void put(Object message, Map<String, Object> headers, Map<String, Object> properties);
}
