package se.ugli.jocote;

public interface Subscription<T> extends AutoCloseable {
    @Override
    void close();
}
