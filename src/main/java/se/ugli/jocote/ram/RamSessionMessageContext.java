package se.ugli.jocote.ram;

import java.util.Queue;

import se.ugli.jocote.Message;
import se.ugli.jocote.SessionContext;

class RamSessionMessageContext implements SessionContext {
    private final Queue<RamMessage> connectionQueue;
    private final RamMessage message;
    private boolean closable;

    RamSessionMessageContext(final RamMessage message, final Queue<RamMessage> connectionQueue) {
        this.message = message;
        this.connectionQueue = connectionQueue;
    }

    @Override
    public Message message() {
        return message;
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
