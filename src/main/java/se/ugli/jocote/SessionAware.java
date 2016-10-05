package se.ugli.jocote;

public interface SessionAware extends AutoCloseable {

    @Override
    void close();

    void ack();

    void nack();

    String sessionid();

}
