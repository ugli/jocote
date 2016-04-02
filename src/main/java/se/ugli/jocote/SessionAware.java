package se.ugli.jocote;

public interface SessionAware {

    void ack();

    void nack();
}
