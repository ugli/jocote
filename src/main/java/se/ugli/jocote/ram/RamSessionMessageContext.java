package se.ugli.jocote.ram;

import java.util.Queue;

import se.ugli.jocote.SessionMessageContext;

class RamSessionMessageContext extends RamMessageContext implements SessionMessageContext {
    private final Queue<RamMessage> connectionQueue;
    private final RamMessage message;
    private boolean closable;

    RamSessionMessageContext(final RamMessage message, final Queue<RamMessage> connectionQueue) {
        super(message);
        this.message = message;
        this.connectionQueue = connectionQueue;
    }

    @Override
    public void acknowledgeMessage() {
        closable = true;
    }

    @Override
    public void leaveMessage() {
        connectionQueue.offer(message);
        closable = true;
    }

    boolean isClosable() {
        return closable;
    }
}
