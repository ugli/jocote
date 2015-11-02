package se.ugli.jocote;

import java.io.Closeable;

public interface SessionIterator<T> extends Iterator<T>, Closeable {

    void acknowledgeMessages();

    void leaveMessages();
}
