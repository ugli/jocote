package se.ugli.jocote;

import java.io.Closeable;
import java.util.Iterator;

public interface SessionIterator<T> extends Iterator<T>, Closeable {

    void acknowledgeMessages();

    void leaveMessages();
}
