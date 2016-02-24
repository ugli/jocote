package se.ugli.jocote;

public interface SessionContext {

    Message message();

    void acknowledgeMessage();

    void leaveMessage();
}
