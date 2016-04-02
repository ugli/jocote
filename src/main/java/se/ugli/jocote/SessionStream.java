package se.ugli.jocote;

public interface SessionStream extends MessageStream, SessionAware, AutoCloseable {

    @Override
    void close();

}
