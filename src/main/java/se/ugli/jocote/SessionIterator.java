package se.ugli.jocote;

public interface SessionIterator<T> extends Iterator<T>, AutoCloseable {

    @Override
    void close();

    void acknowledgeMessages();

    void leaveMessages();
}
