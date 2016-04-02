package se.ugli.jocote.ram;

import java.util.Optional;
import java.util.Queue;

import se.ugli.jocote.Message;
import se.ugli.jocote.support.MessageIterator;

public class RamIterator implements MessageIterator {

    private final Queue<Message> connectionQueue;
    private int index = 0;

    public RamIterator(final Queue<Message> connectionQueue) {
        this.connectionQueue = connectionQueue;
    }

    @Override
    public Optional<Message> next() {
        final Message message = connectionQueue.poll();
        if (message != null) {
            index++;
            return Optional.of(message);
        }
        return Optional.empty();
    }

    @Override
    public int index() {
        return index;
    }

}
