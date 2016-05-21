package se.ugli.jocote.ram;

import se.ugli.jocote.Message;
import se.ugli.jocote.MessageIterator;

import java.util.Optional;
import java.util.Queue;

class RamIterator implements MessageIterator {

    private final Queue<Message> connectionQueue;

    RamIterator(final Queue<Message> connectionQueue) {
        this.connectionQueue = connectionQueue;
    }

    @Override
    public Optional<Message> next() {
        return Optional.ofNullable(connectionQueue.poll());
    }

}
