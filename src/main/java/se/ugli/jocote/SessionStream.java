package se.ugli.jocote;

import java.util.stream.Stream;

public interface SessionStream extends Stream<Message>, AutoCloseable {

    @Override
    void close();

    void acknowledgeMessages();

    void leaveMessages();

}
