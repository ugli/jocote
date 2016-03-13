package se.ugli.jocote;

import java.util.stream.Stream;

public interface SessionStream extends Stream<byte[]>, AutoCloseable {

    @Override
    void close();

    void acknowledgeMessages();

    void leaveMessages();

}
