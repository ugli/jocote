package se.ugli.jocote;

public interface Subscription extends AutoCloseable {
    @Override
    void close();
}
