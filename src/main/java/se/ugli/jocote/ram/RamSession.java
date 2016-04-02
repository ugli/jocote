package se.ugli.jocote.ram;

import java.util.Queue;

import se.ugli.jocote.Message;
import se.ugli.jocote.Session;

class RamSession implements Session {
    private final Queue<Message> connectionQueue;
    private final Message message;
    private boolean closable;

    RamSession(final Message message, final Queue<Message> connectionQueue) {
        this.message = message;
        this.connectionQueue = connectionQueue;
    }

    @Override
    public Message message() {
        return message;
    }

    @Override
    public void ack() {
        closable = true;
    }

    @Override
    public void nack() {
        connectionQueue.offer(message);
        closable = true;
    }

    boolean isClosable() {
        return closable;
    }
}
