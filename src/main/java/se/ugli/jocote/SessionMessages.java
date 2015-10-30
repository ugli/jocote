package se.ugli.jocote;

import java.io.Closeable;

public interface SessionMessages extends Closeable {

    void acknowledgeMessages();

    void leaveMessages();
}
