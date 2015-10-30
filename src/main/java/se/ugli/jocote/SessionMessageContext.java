package se.ugli.jocote;

public interface SessionMessageContext extends MessageContext {
    void acknowledgeMessage();

    void leaveMessage();
}
