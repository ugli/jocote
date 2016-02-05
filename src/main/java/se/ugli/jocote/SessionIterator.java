package se.ugli.jocote;

import java.io.Closeable;

public interface SessionIterator<T> extends Iterator<T>, Closeable {

    @Override
    void close();

    void acknowledgeMessages();

    void leaveMessages();
}
